package org.treblereel.gwt.crysknife.generator.context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.lang.model.element.TypeElement;

import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.WiringElementType;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
public class IOCContext {

    private final Map<IOCGeneratorMeta, IOCGenerator> generators = new HashMap<>();

    private final Map<TypeElement, BeanDefinition> beans = new HashMap<>();

    private final Map<TypeElement, Map<String, BeanDefinition>> qualifiers = new HashMap<>();

    private final GenerationContext generationContext;

    private final List<TypeElement> orderedBeans = new LinkedList<>();

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

    public Map<IOCGeneratorMeta, IOCGenerator> getGenerators() {
        return generators;
    }

    public Map<TypeElement, BeanDefinition> getBeans() {
        return beans;
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
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            IOCGeneratorMeta that = (IOCGeneratorMeta) o;
            return Objects.equals(annotation, that.annotation) &&
                    wiringElementType == that.wiringElementType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(annotation, wiringElementType);
        }
    }
}
