package org.treblereel.gwt.crysknife.generator.definition;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public abstract class Definition {

    public static Comparator<IOCGenerator> iOCGeneratorcomparator = Comparator.comparing(h -> h.getClass().getAnnotation(Generator.class).priority());

    protected final Map<IOCGenerator, Set<ExecutableDefinition>> executableDefinitions = new HashMap<>();

    protected Set<BeanDefinition> dependsOn = new LinkedHashSet<>();

    protected Optional<IOCGenerator> generator = Optional.empty();

    protected Map<IOCGenerator, Definition> decorators = new HashMap<>();

    public void setGenerator(IOCGenerator generator) {
        this.generator = Optional.of(generator);
    }

    public void generate(ClassBuilder builder) {
        generator.ifPresent(gen -> gen.generate(builder, this));
    }

    public void generateDecorators(ClassBuilder builder) {
        decorators.keySet().stream().sorted(iOCGeneratorcomparator)
                .forEach(decorator -> decorator.generate(builder, this));
    }

    public <T extends Definition> T addDecorator(IOCGenerator generator, Definition definition) {
        decorators.put(generator, definition);
        return (T) this;
    }
}
