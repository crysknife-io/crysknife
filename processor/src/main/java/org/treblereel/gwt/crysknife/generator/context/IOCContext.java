package org.treblereel.gwt.crysknife.generator.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.WiringElementType;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
public class IOCContext {

    private final SetMultimap<IOCGeneratorMeta, IOCGenerator> generators = HashMultimap.create();

    private final Map<TypeElement, BeanDefinition> beans = new HashMap<>();

    private final Map<TypeElement, Map<String, BeanDefinition>> qualifiers = new HashMap<>();

    private final GenerationContext generationContext;

    private final List<TypeElement> orderedBeans = new LinkedList<>();

    private final List<String> blacklist = new ArrayList<>();

    public IOCContext(GenerationContext generationContext) {
        this.generationContext = generationContext;
    }

    public void register(final Class annotation, final WiringElementType wiringElementType, final IOCGenerator generator) {
        register(annotation, Object.class, wiringElementType, generator);
    }

    public void register(final Class annotation, Class exactType, final WiringElementType wiringElementType, final IOCGenerator generator) {
        TypeElement type = getGenerationContext()
                .getElements()
                .getTypeElement(exactType.getCanonicalName());
        this.generators.put(new IOCGeneratorMeta(annotation.getCanonicalName(), type, wiringElementType), generator);
    }

    public SetMultimap<IOCGeneratorMeta, IOCGenerator> getGenerators() {
        return generators;
    }

    public Map<TypeElement, BeanDefinition> getBeans() {
        return beans;
    }

    public BeanDefinition getBean(TypeElement bean) {
        if (beans.containsKey(bean)) {
            return beans.get(bean);
        }
        throw new Error(bean.toString());
    }

    public GenerationContext getGenerationContext() {
        return generationContext;
    }

    public Map<TypeElement, Map<String, BeanDefinition>> getQualifiers() {
        return qualifiers;
    }

    public List<TypeElement> getOrderedBeans() {
        return orderedBeans;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }

    public BeanDefinition getBeanDefinitionOrCreateAndReturn(TypeElement typeElement) {
        BeanDefinition beanDefinition;
        if (getBeans().containsKey(typeElement)) {
            beanDefinition = getBeans().get(typeElement);
        } else {
            beanDefinition = BeanDefinition.of(typeElement, this);
            getBeans().put(typeElement, beanDefinition);
        }
        checkNamedAndAdd(typeElement, beanDefinition);
        return beanDefinition;
    }

    private void checkNamedAndAdd(TypeElement typeElement, BeanDefinition beanDefinition) {
        if (typeElement.getAnnotation(Named.class) != null) {
            String named = typeElement.getAnnotation(Named.class).value();
            typeElement.getInterfaces().stream().forEach(i -> {
                Element asElement = MoreTypes.asElement(i);
                TypeElement iface = MoreElements.asType(asElement);
                if (!getQualifiers().containsKey(iface)) {
                    getQualifiers().put(iface, new HashMap<>());
                }
                getQualifiers().get(iface).put(named, beanDefinition);
            });
        }
    }

    public static class IOCGeneratorMeta {

        public final String annotation;
        public final TypeElement exactType;
        public final WiringElementType wiringElementType;

        public IOCGeneratorMeta(String annotation, TypeElement exactType, WiringElementType wiringElementType) {
            this.annotation = annotation;
            this.wiringElementType = wiringElementType;
            this.exactType = exactType;
        }

        @Override
        public String toString() {
            return "IOCGeneratorMeta{" +
                    "annotation='" + annotation + '\'' +
                    ", exactType=" + exactType +
                    ", wiringElementType=" + wiringElementType +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IOCGeneratorMeta)) {
                return false;
            }
            IOCGeneratorMeta that = (IOCGeneratorMeta) o;
            return Objects.equals(annotation, that.annotation) &&
                    Objects.equals(exactType, that.exactType) &&
                    wiringElementType == that.wiringElementType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(annotation, exactType, wiringElementType);
        }
    }
}
