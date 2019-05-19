package org.treblereel.gwt.crysknife.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

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

/*        for (Map.Entry<TypeElement, BeanDefinition> entry : iocContext.getBeans().entrySet()) {
            BeanDefinition bean = entry.getValue();
            if (bean.getConstructorInjectionPoint() != null) {
                bean.getConstructorInjectionPoint()
                        .getArguments()
                        .forEach(field -> processFieldInjectionPoint(field, bean));
            }
        }*/
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
        if (field.getType().getKind().isInterface() || field.isNamed()) {

            System.out.println("processFieldInjectionPoint " + field.getType() + " " + definition.getQualifiedName());

            TypeElement dependency = null;
            if (field.isNamed()) {
                String named = field.getNamed();
                dependency = iocContext.getQualifiers().get(field.getType()).get(named).getType();
            } else if (iocContext.getQualifiers().containsKey(field.getType())
                    && iocContext.getQualifiers().get(field.getType()).containsKey(Default.class.getCanonicalName())) {
                dependency = iocContext.getQualifiers().get(field.getType()).get(Default.class.getCanonicalName()).getType();
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
                    dependency = iocContext.getBeans().get(result.get()).getType();
                } else {
                    Optional<TypeElement> iface = iocContext.getBeans()
                            .keySet()
                            .stream()
                            .filter(bean -> types.isSubtype(bean.asType(), beanType))
                            .findFirst();
                    dependency = iocContext.getBeans().get(iface.get()).getType();
                }

                //add as Default if not exist
                if (!iocContext.getQualifiers().containsKey(field.getType())) {
                    Optional<TypeElement> subType = iocContext.getBeans()
                            .keySet()
                            .stream()
                            .filter(elm -> (!elm.equals(field.getType()) && types.isSubtype(elm.asType(), field.getType().asType()))).findFirst();

                    if (subType.isPresent()) {
                        Map<String, BeanDefinition> qualifiers = new HashMap<>();
                        qualifiers.put(Default.class.getCanonicalName(), iocContext.getBeans().get(dependency));
                        iocContext.getQualifiers().put(field.getType(), qualifiers);
                    }
                }
            }
            if (dependency == null) {
                DeclaredType type = MoreTypes.asDeclared(field.getField().asType());
                throw new Error("Unable find implementation of bean " + type + " from " + field.getField().getEnclosingElement());
            }

            if (field.isNamed()) {
                BeanDefinition named = iocContext.getQualifiers().get(field.getType()).get(field.getNamed());
                dependency = named.getType();
                definition.getDependsOn().remove(iocContext.getBeans().get(field.getType()));
                definition.getDependsOn().add(named);
            } else {
                field.setType(dependency);
                definition.getDependsOn().add(iocContext.getBeans().get(dependency));
            }

            System.out.println(" ? " + field.getType() + " " + iocContext.getBeans().get(dependency));

            field.setType(dependency);
        }

        if (!iocContext.getBeans().containsKey(field.getType())) {
            unmanaged.add(field.getType());
        }
    }
}
