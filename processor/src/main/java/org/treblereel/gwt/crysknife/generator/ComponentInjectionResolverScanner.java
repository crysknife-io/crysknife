package org.treblereel.gwt.crysknife.generator;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/5/19
 */
public class ComponentInjectionResolverScanner {

    private final IOCContext iocContext;

    Set<TypeElement> unmanaged = new HashSet<>();

    public ComponentInjectionResolverScanner(IOCContext iocContext) {
        this.iocContext = iocContext;
    }

    public void scan() {
        iocContext.getBeans().forEach((type, bean) -> {
            bean.getFieldInjectionPoints().forEach(
                    field -> processFieldInjectionPoint(field, bean));
        });

        iocContext.getBeans().forEach((type, bean) -> {
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
            BeanDefinition beanDefinition = BeanDefinition.of(bean, iocContext);
            IOCGenerator gen = iocContext.getGenerators().get(meta);
            beanDefinition.addGenerator(gen);
            iocContext.getBeans().put(bean, beanDefinition);
        });
    }

    private void processFieldInjectionPoint(FieldPoint field, BeanDefinition definition) {
        if (field.getType().getKind().isInterface() || field.isNamed()) {
            TypeElement dependency = null;
            if (field.isNamed()) {
                String named = field.getNamed();
                dependency = iocContext.getQualifiers().get(field.getType()).get(named).getType();
            } else if (field.getType().getKind().isInterface()) {
                TypeMirror beanType = field.getType().asType();
                Types types = iocContext.getGenerationContext().getTypes();
                Optional<TypeElement> result = iocContext.getBeans().keySet().stream().filter(bean -> types.isSubtype(bean.asType(), beanType)).findFirst();

                if (result.isPresent()) {
                    dependency = iocContext.getBeans().get(result.get()).getType();
                } else {
                    TypeElement typeElement = MoreElements.asType(types.asElement(beanType));
                    Optional<IOCContext.IOCGeneratorMeta> meta = iocContext.getGenerators().keySet().stream().filter(k -> k.exactType.equals(typeElement)).findFirst();
                    if (meta.isPresent()) {
                        System.out.println("Will be generated " + definition.getDeclaredTypes().stream().map(m -> m.toString()).collect(Collectors.joining(",")));
                    }
                }
            }
            if (dependency == null) {
                DeclaredType type = MoreTypes.asDeclared(field.getField().asType());

                throw new Error("Unable find implementation of bean " + type + " from " + field.getField().getEnclosingElement());
            }

            definition.getDependsOn().remove(field.getType());
            definition.getDependsOn().add(iocContext.getBeans().get(dependency));
            field.setType(dependency);
        }

        if (!iocContext.getBeans().containsKey(field.getType())) {
            unmanaged.add(field.getType());
        }
    }
}
