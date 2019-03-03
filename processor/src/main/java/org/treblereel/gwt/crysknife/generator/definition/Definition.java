package org.treblereel.gwt.crysknife.generator.definition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.definition.ExecutableDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public abstract class Definition {

    protected final Set<IOCGenerator> generators = new HashSet<>();

    protected final Map<IOCGenerator, Set<ExecutableDefinition>> executableDefinitions = new HashMap<>();

    public void addGenerator(IOCGenerator generator) {
        generators.add(generator);
    }

    public void generate(ClassBuilder builder) {
        generators.forEach(generator -> {
            generator.generate(builder, this);
        });

        executableDefinitions.forEach(((generator, executables) -> executables.forEach(executable -> {
            generator.generate(builder, executable);
        })));
    }

    public void finish(ClassBuilder builder) {
        generators.forEach(generator -> {
            generator.finish(builder, this);
        });

        executableDefinitions.forEach(((generator, executables) -> executables.forEach(executable -> {
            generator.finish(builder, executable);
        })));
    }
}
