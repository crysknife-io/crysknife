package org.treblereel.gwt.crysknife.generator.definition;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public abstract class Definition {

    protected final Map<IOCGenerator, Set<ExecutableDefinition>> executableDefinitions = new HashMap<>();
    protected Set<BeanDefinition> dependsOn = new LinkedHashSet<>();
    protected Optional<IOCGenerator> generator = Optional.empty();

    public void setGenerator(IOCGenerator generator) {
        this.generator = Optional.of(generator);
    }

    public void generate(ClassBuilder builder) {
        generator.ifPresent(gen -> gen.generate(builder, this));
    }
}
