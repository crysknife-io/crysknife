package org.treblereel.gwt.crysknife.internal;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;

import javax.annotation.PostConstruct;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
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

    private final Map<String, BeanDefinition> beans = new HashMap<>();

    private final Map<String, ProducerDefinition> producers = new HashMap<>();

    private final Map<String, NamedDefinition> named = new HashMap<>();

    private final Set<String> packages;

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
        processProducersAnnotation();
        processNamedAnnotation();
        processSingletonAnnotation();
    }

    private void processProducersAnnotation() {
        Set<ExecutableElement> producers = (Set<ExecutableElement>) roundEnvironment.getElementsAnnotatedWith(Produces.class);
        producers.stream().forEach(pro -> {
            TypeElement returnType = MoreElements.asType(MoreTypes.asElement(pro.getReturnType()));
            ProducerDefinition producer = new ProducerDefinition(returnType, MoreElements.asType(pro.getEnclosingElement()), pro.getSimpleName().toString());
            this.producers.put(getQualifiedName(returnType), producer);
        });
    }

    private void processNamedAnnotation() {
        Set<Element> producers = (Set<Element>) roundEnvironment.getElementsAnnotatedWith(Named.class);
        producers.stream().forEach(named -> {
            if (named.getKind().equals(ElementKind.CLASS)) {
                TypeElement element = MoreElements.asType(named);
                String value = named.getAnnotation(Named.class).value();
                element.getInterfaces().forEach(i -> {
                    String iface = getQualifiedName(MoreTypes.asElement(i));
                    NamedDefinition definition;
                    if (this.named.containsKey(iface)) {
                        definition = this.named.get(iface);
                    } else {
                        definition = new NamedDefinition(iface);
                        this.named.put(iface, definition);
                    }
                    definition.getImplementations().put(value, getQualifiedName(element));
                });
            }
        });
    }

    private void processSingletonAnnotation() {
        Stack<TypeElement> stack = new Stack<>();
        stack.push(application);
        while (!stack.isEmpty()) {
            TypeElement scan = stack.pop();
            BeanDefinition parent = getBeanDefinitionOrCreateNew(scan);
            String parentQualifiedName = getQualifiedName(scan);
            graph.addNode(parentQualifiedName);
            for (Element elm : getAnnotatedElements(processingEnvironment.getElementUtils(), scan, Inject.class, Dependent.class)) {
                if (elm.getKind().equals(ElementKind.CONSTRUCTOR)) {
                    String childQualifiedName = getQualifiedName(elm);
                    graph.addNode(childQualifiedName);
                    if (!childQualifiedName.equals(parentQualifiedName)) {
                        graph.putEdge(parentQualifiedName, childQualifiedName);
                    }
                    ConstructorInjectionPoint point = new ConstructorInjectionPoint(scan,
                            ElementKind.CONSTRUCTOR,
                            elm.getSimpleName().toString());
                    ExecutableElement constructor = (ExecutableElement) elm;
                    List<? extends VariableElement> params = constructor.getParameters();
                    for (int i = 0; i < params.size(); i++) {
                        processConstructorParam(stack, elm, point, params.get(i));
                    }
                    parent.setConstructorInjectionPoint(point);
                } else if (elm.getKind().equals(ElementKind.FIELD)) {
                    processField(stack, scan, parent, elm, parentQualifiedName);
                }
            }
        }

        Traverser.forGraph(graph).depthFirstPostOrder(getQualifiedName(application)).forEach(bean -> {
            orderedBeans.add(bean);
        });
    }

    private void processConstructorParam(Stack<TypeElement> stack, Element elm, ConstructorInjectionPoint point, VariableElement param) {
        TypeElement next;
        String beanName;
        Named named = param.getAnnotation(Named.class);
        DeclaredType declaredType = (DeclaredType) param.asType();
        String name = param.getSimpleName().toString();
        if(named != null){
            String type = named.value();
            beanName = this.named.get(declaredType.toString()).getImplementations().get(type);
            TypeElement element = processingEnvironment.getElementUtils().getTypeElement(beanName);
            getBeanDefinitionOrCreateNew(element);
            next = element;
        } else {
            next = (TypeElement) declaredType.asElement();
            beanName = getQualifiedName(next);
        }
        graph.addNode(beanName);
        graph.putEdge(getQualifiedName(elm), beanName);
        point.addParam(name, MoreTypes.asDeclared(next.asType()));
        stack.push(next);
    }

    private void processField(Stack<TypeElement> stack, TypeElement scan, BeanDefinition parent, Element elm, String parentQualifiedName) {
        TypeElement next;
        FieldInjectionPoint point;
        String childQualifiedName;
        Named named = elm.getAnnotation(Named.class);
        DeclaredType declaredType = (DeclaredType) elm.asType();

        if (named != null) {
            String beanType = named.value();
            String bean = this.named.get(declaredType.toString()).getImplementations().get(beanType);
            TypeElement element = processingEnvironment.getElementUtils().getTypeElement(bean);
            getBeanDefinitionOrCreateNew(element);
            point = new FieldInjectionPoint(scan,
                    ElementKind.FIELD,
                    elm.getSimpleName().toString(),
                    element);
            next = element;
            childQualifiedName = getQualifiedName(element);
        } else {
            point = new FieldInjectionPoint(scan,
                    ElementKind.FIELD,
                    elm.getSimpleName().toString(),
                    (TypeElement) declaredType.asElement());
            next = (TypeElement) declaredType.asElement();
            childQualifiedName = getQualifiedName(elm);
        }
        graph.addNode(childQualifiedName);
        if (!childQualifiedName.equals(parentQualifiedName)) {
            graph.putEdge(parentQualifiedName, childQualifiedName);
        }
        parent.getFieldInjectionPoints().add(point);
        stack.push(next);
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
        Dependent dependent = element.getAnnotation(Dependent.class);
        if (singleton != null) {
            bean.setType(BeanType.SINGLETON);
        }else if(dependent != null) {
            bean.setType(BeanType.DEPENDENT);
        } else if (producers.containsKey(getQualifiedName(element))) {
            bean.setType(BeanType.PRODUCIBLE);
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

    public Map<String, ProducerDefinition> getProducers() {
        return producers;
    }
}
