package org.treblereel.gwt.crysknife.generator;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/5/19
 */
public class ComponentInjectionResolverScanner {

    private final IOCContext iocContext;

    private Set<TypeElement> unmanaged = new HashSet<>();

    public ComponentInjectionResolverScanner(IOCContext iocContext) {
        this.iocContext = iocContext;
    }

    public void scan() {
        iocContext.getBeans().forEach((type, bean) -> {
            for (FieldPoint field : bean.getFieldInjectionPoints()) {
                processFieldInjectionPoint(field, bean);
            }
            if (bean.getConstructorInjectionPoint() != null) {
                bean.getConstructorInjectionPoint()
                        .getArguments()
                        .forEach(field -> processFieldInjectionPoint(field, bean));
            }
        });

        addUnmanagedBeans();
    }

    //Process as Dependent Beans //TODO
    private void addUnmanagedBeans() {
        TypeElement type = iocContext
                .getGenerationContext()
                .getElements()
                .getTypeElement(Object.class.getCanonicalName());

        IOCContext.IOCGeneratorMeta meta = new IOCContext.IOCGeneratorMeta(Dependent.class.getCanonicalName(),
                                                                           type,
                                                                           WiringElementType.DEPENDENT_BEAN);

        unmanaged.forEach(bean -> {
            BeanDefinition beanDefinition = iocContext.getBeanDefinitionOrCreateAndReturn(bean);
            if (iocContext.getGenerators().get(meta).stream().findFirst().isPresent()) {
                IOCGenerator gen = iocContext.getGenerators().get(meta).stream().findFirst().get();
                beanDefinition.setGenerator(gen);
                iocContext.getBeans().put(bean, beanDefinition);
            } else {
                throw new Error("Unable to find generator based on meta " + meta.toString());
            }
        });
    }

    private void processFieldInjectionPoint(FieldPoint field, BeanDefinition definition) {
        BeanDefinition beanDefinition = null;
        if (field.isNamed()) {
            beanDefinition = iocContext.getBeans().get(field.getType());
        } else if (iocContext.getQualifiers().containsKey(field.getType())) {
            beanDefinition = getTypeQualifierValue(field.getField(), iocContext.getQualifiers().get(field.getType()));
        } else if (field.getType().getKind().isInterface()) {
            TypeMirror beanType = field.getType().asType();
            Types types = iocContext.getGenerationContext().getTypes();
            Optional<TypeElement> result = iocContext.getBeans()
                    .keySet()
                    .stream()
                    .filter(bean -> types.isSubtype(bean.asType(), beanType))
                    .filter(elm -> elm.getKind().equals(ElementKind.CLASS))
                    .findFirst();
            if (result.isPresent()) {
                beanDefinition = iocContext.getBeans().get(result.get());
            }
        }

        if (beanDefinition != null) {
            definition.getDependsOn().add(beanDefinition);
            field.setType(beanDefinition.getType());
        }
        if (!iocContext.getBeans().containsKey(field.getType())) {
            unmanaged.add(field.getType());
        }
    }

    private BeanDefinition getTypeQualifierValue(VariableElement element, Map<String, BeanDefinition> qualifiers) {

        for (AnnotationMirror annotation : iocContext.getGenerationContext()
                .getElements()
                .getAllAnnotationMirrors(element)) {
            if (qualifiers.containsKey(annotation.toString())) {
                return qualifiers.get(annotation.toString());
            }
        }
        return qualifiers.get(Default.class.getCanonicalName());
    }
}
