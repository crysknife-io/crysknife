package org.treblereel.gwt.crysknife.generator;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.lang.model.element.TypeElement;
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

    Set<TypeElement> unmannaged = new HashSet<>();

    public ComponentInjectionResolverScanner(IOCContext iocContext) {
        this.iocContext = iocContext;
    }

    public void scan() {
        Set<TypeElement> processed = new HashSet<>();

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

        addUnmannagedBeans();
    }

    //Process as Dependent Beans
    private void addUnmannagedBeans() {
        IOCContext.IOCGeneratorMeta meta = new IOCContext.IOCGeneratorMeta(Dependent.class.getCanonicalName(), WiringElementType.DEPENDENT_BEAN);
        unmannaged.forEach(bean -> {
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
                }
            }
            if (dependency == null) {
                throw new Error("Unable find implementation of bean " + Utils.getQualifiedName(field.getType()) + " from " + field.getField().getEnclosingElement());
            }
            definition.getDependsOn().remove(field.getType());
            definition.getDependsOn().add(dependency);
            field.setType(dependency);
        }

        if (!iocContext.getBeans().containsKey(field.getType())) {
            unmannaged.add(field.getType());
        }
    }
}
