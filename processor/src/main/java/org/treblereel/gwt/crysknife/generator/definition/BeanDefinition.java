package org.treblereel.gwt.crysknife.generator.definition;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.point.ConstructorPoint;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/20/19
 */
public class BeanDefinition extends Definition {

    private Set<TypeElement> dependsOn = new LinkedHashSet<>();
    private List<FieldPoint> fieldInjectionPoints = new LinkedList<>();
    private ConstructorPoint constructorInjectionPoint;
    private String className;
    private String classFactoryName;
    private String packageName;
    private String qualifiedName;
    private TypeElement element;

    protected BeanDefinition(TypeElement element) {
        this.element = element;
        this.className = element.getSimpleName().toString();
        this.classFactoryName = Utils.getFactoryClassName(element);
        this.packageName = Utils.getPackageName(element);
        this.qualifiedName = Utils.getQualifiedName(element);
    }

    public static BeanDefinition of(TypeElement element, IOCContext context) {
        return new BeanDefinitionBuilder(element, context).build();
    }

    private static void addDependency(BeanDefinition beanDefinition, VariableElement variable, Elements elements) {
        TypeElement type = MoreElements.asType(MoreTypes.asElement(variable.asType()));
        beanDefinition.dependsOn.add(type);
    }

    public void addExecutableDefinition(IOCGenerator generator, ExecutableDefinition definition) {
        if (executableDefinitions.containsKey(generator)) {
            executableDefinitions.get(generator).add(definition);
        } else {
            executableDefinitions.put(generator, new HashSet());
            executableDefinitions.get(generator).add(definition);
        }
    }

    public void addGenerator(IOCGenerator iocGenerator) {
        generators.add(iocGenerator);
    }

    @Override
    public String toString() {
        return "BeanDefinition {" +
                " generators = [ " + generators.stream().map(m -> m.getClass().getSimpleName()).collect(Collectors.joining(", ")) +
                " ] , element= [" + element +
                " ] , dependsOn= [ " + dependsOn.stream().map(m -> Utils.getQualifiedName(m)).collect(Collectors.joining(", ")) +
                " ] , executables= [ " + executableDefinitions.values().stream().map(m -> m.toString()).collect(Collectors.joining(", ")) +
                " ]}";
    }

    public Set<TypeElement> getDependsOn() {
        return dependsOn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Utils.getQualifiedName(element));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanDefinition that = (BeanDefinition) o;
        return Objects.equals(Utils.getQualifiedName(element), Utils.getQualifiedName(that.element));
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public Map<IOCGenerator, Set<ExecutableDefinition>> getExecutableDefinitions() {
        return executableDefinitions;
    }

    public String getClassFactoryName() {
        return classFactoryName;
    }

    public List<FieldPoint> getFieldInjectionPoints() {
        return fieldInjectionPoints;
    }

    public TypeElement getType() {
        return element;
    }

    public ConstructorPoint getConstructorInjectionPoint() {
        return constructorInjectionPoint;
    }

    private static class BeanDefinitionBuilder {

        private BeanDefinition beanDefinition;

        private Elements elements;

        BeanDefinitionBuilder(TypeElement element, IOCContext context) {
            this.beanDefinition = new BeanDefinition(element);
            this.elements = context.getGenerationContext().getElements();
        }

        public BeanDefinition build() {
            processInjections();
            return beanDefinition;
        }

        private void processInjections() {
            elements.getAllMembers(beanDefinition.element).forEach(mem -> {
                if (mem.getAnnotation(Inject.class) != null && (mem.getKind().equals(ElementKind.CONSTRUCTOR) || mem.getKind().equals(ElementKind.FIELD))) {
                    if (mem.getModifiers().contains(Modifier.PRIVATE) || mem.getModifiers().contains(Modifier.PROTECTED)) {
                        throw new Error(mem.toString());
                    }
                    if (mem.getKind().equals(ElementKind.CONSTRUCTOR)) {
                        ExecutableElement elms = MoreElements.asExecutable(mem);
                        beanDefinition.constructorInjectionPoint = new ConstructorPoint(Utils.getQualifiedName(beanDefinition.element), beanDefinition.element);

                        for (int i = 0; i < elms.getParameters().size(); i++) {
                            FieldPoint field = parseField(elms.getParameters().get(i));
                            beanDefinition.getConstructorInjectionPoint().addArgument(field);
                        }
                    } else if (mem.getKind().equals(ElementKind.FIELD)) {
                        FieldPoint fiend = parseField(mem);
                        beanDefinition.fieldInjectionPoints.add(fiend);
                    }
                }
            });
        }

        public FieldPoint parseField(Element type) {
            FieldPoint field = FieldPoint.of(MoreElements.asVariable(type));
            if (!field.isNamed()) {
                addDependency(beanDefinition, MoreElements.asVariable(type), elements);
            }
            return field;
        }
    }
}
