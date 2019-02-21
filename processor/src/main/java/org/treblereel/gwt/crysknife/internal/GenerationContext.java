package org.treblereel.gwt.crysknife.internal;

import com.google.auto.common.MoreElements;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;

import javax.annotation.PostConstruct;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static org.treblereel.gwt.crysknife.ApplicationProcessor.getAnnotatedElements;
import static org.treblereel.gwt.crysknife.internal.Utils.getQualifiedName;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class GenerationContext {

    private final RoundEnvironment roundEnvironment;

    private final ProcessingEnvironment processingEnvironment;

    private final Set<String> packages;

    private final Map<String, BeanDefinition> beans = new HashMap<>();

    private final MutableGraph<String> graph = GraphBuilder.directed().build();

    private final List<String> orderedBeans = new LinkedList<>();


    private TypeElement application;

    public GenerationContext(RoundEnvironment roundEnvironment,
                             ProcessingEnvironment processingEnvironment,
                             Set<String> packages,
                             TypeElement application) {
        this.roundEnvironment = roundEnvironment;
        this.processingEnvironment = processingEnvironment;
        this.application = application;
        this.packages = packages;

        init();
    }

    private void init() {
        processSingletonAnnotation();

        //graph();

    }

/*    private void graph() {
        MutableGraph<String> graph = GraphBuilder.directed().build();

        Stack<TypeElement> stack = new Stack<>();
        stack.push(application);

        while (!stack.isEmpty()) {
            TypeElement scan = stack.pop();
            String parentQualifiedName = getQualifiedName(scan);
            System.out.println("Parent " + parentQualifiedName);
            graph.addNode(parentQualifiedName);

            for (Element elm : getAnnotatedElements(processingEnvironment.getElementUtils(), scan, Inject.class)) {
                String childQualifiedName = getQualifiedName(elm);
                System.out.println("Child " + childQualifiedName);
                graph.addNode(childQualifiedName);

                if (!childQualifiedName.equals(parentQualifiedName)) {
                    System.out.println("Child to Parent  " + childQualifiedName + " " + parentQualifiedName);

                    graph.putEdge(parentQualifiedName, childQualifiedName);
                }
                if (elm.getKind().equals(ElementKind.CONSTRUCTOR)) {
                    ExecutableElement constructor = (ExecutableElement) elm;
                    List<? extends VariableElement> params = constructor.getParameters();
                    for (int i = 0; i < params.size(); i++) {

                        DeclaredType declaredType = (DeclaredType) params.get(i).asType();
                        graph.addNode(declaredType.toString());
                        graph.putEdge(getQualifiedName(elm), declaredType.toString());

                        stack.push((TypeElement) declaredType.asElement());
                    }
                } else if (elm.getKind().equals(ElementKind.FIELD)) {
                    DeclaredType declaredType = (DeclaredType) elm.asType();
                    stack.push((TypeElement) declaredType.asElement());
                }
            }
        }



        Traverser.forGraph(graph).depthFirstPostOrder(getQualifiedName(application)).forEach(n -> {
            System.out.println(" kk " + n);
        });


    }*/

    private void processSingletonAnnotation() {
        Stack<TypeElement> stack = new Stack<>();
        stack.push(application);
        while (!stack.isEmpty()) {
            TypeElement scan = stack.pop();
            BeanDefinition parent = getBeanDefinitionOrCreateNew(scan);
            String parentQualifiedName = getQualifiedName(scan);
            graph.addNode(parentQualifiedName);
            for (Element elm : getAnnotatedElements(processingEnvironment.getElementUtils(), scan, Inject.class)) {
                String childQualifiedName = getQualifiedName(elm);
                graph.addNode(childQualifiedName);
                if (!childQualifiedName.equals(parentQualifiedName)) {
                    graph.putEdge(parentQualifiedName, childQualifiedName);
                }
                if (elm.getKind().equals(ElementKind.CONSTRUCTOR)) {
                    ConstructorInjectionPoint point = new ConstructorInjectionPoint(scan,
                            ElementKind.CONSTRUCTOR,
                            elm.getSimpleName().toString());
                    ExecutableElement constructor = (ExecutableElement) elm;
                    List<? extends VariableElement> params = constructor.getParameters();
                    for (int i = 0; i < params.size(); i++) {
                        String name = params.get(i).getSimpleName().toString();
                        DeclaredType declaredType = (DeclaredType) params.get(i).asType();
                        graph.addNode(declaredType.toString());
                        graph.putEdge(getQualifiedName(elm), declaredType.toString());
                        stack.push((TypeElement) declaredType.asElement());
                        point.addParam(name, declaredType);
                    }
                    parent.setConstructorInjectionPoint(point);
                } else if (elm.getKind().equals(ElementKind.FIELD)) {
                    DeclaredType declaredType = (DeclaredType) elm.asType();
                    FieldInjectionPoint point = new FieldInjectionPoint(scan,
                            ElementKind.FIELD,
                            elm.getSimpleName().toString(),
                            (TypeElement) declaredType.asElement());
                    parent.getFieldInjectionPoints().add(point);
                    stack.push((TypeElement) declaredType.asElement());
                }
            }
        }

        Traverser.forGraph(graph).depthFirstPostOrder(getQualifiedName(application)).forEach(n -> {
            orderedBeans.add(n);
        });
    }

    private BeanDefinition getBeanDefinitionOrCreateNew(Element scan) {
        String name = getQualifiedName(scan);
        if (beans.containsKey(name)) {
            return beans.get(name);
        } else {
            BeanDefinition bean = new BeanDefinition(scan);
            setPostConstruct(bean);
            setBeanType(bean);
            beans.put(name, bean);
            return bean;
        }
    }

    private void setBeanType(BeanDefinition bean) {
        TypeElement element = MoreElements.asType(bean.getElement());
        Singleton singleton = element.getAnnotation(Singleton.class);
        if (singleton != null) {
            bean.setType(BeanType.SINGLETON);
        } else {
            bean.setType(BeanType.DEPENDENT);
        }
    }

    private void setPostConstruct(BeanDefinition bean) {
        TypeElement element = MoreElements.asType(bean.getElement());
        Set<Element> methods = getAnnotatedElements(processingEnvironment.getElementUtils(),
                element,
                PostConstruct.class);
        if (methods.size() > 1) {
            throw new Error("Only one method in '" + getQualifiedName(element) + "' annotated as @PostConstruct must be present");
        }
        if (methods.size() == 1) {
            ExecutableElement method = MoreElements.asExecutable(methods.stream().findFirst().get());
            if (method.getParameters().size() > 0) {
                throw new Error("Method '" + method.getSimpleName().toString() + "' in '" + getQualifiedName(element) + "' annotated as @PostConstruct contains params.");
            }
            method.getModifiers().stream().forEach(modifier -> {
                if (modifier.equals(Modifier.PRIVATE)) {
                    throw new Error("Method '" + method.getSimpleName().toString() + "' in '" + getQualifiedName(element) + "'annotated as @PostConstruct must have public or default access.");
                }
            });
            bean.setPostConstract(method.getSimpleName().toString());
        }
    }

    public Map<String, BeanDefinition> getBeans() {
        return beans;
    }

    public List<String> getOrderedBeans() {
        return orderedBeans;
    }
}
