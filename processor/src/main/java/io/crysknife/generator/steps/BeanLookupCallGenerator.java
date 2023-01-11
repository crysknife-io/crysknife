package io.crysknife.generator.steps;

import com.github.javaparser.ast.expr.Expression;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.GenerationUtils;

public abstract class BeanLookupCallGenerator {

    protected final IOCContext context;
    protected final GenerationUtils generationUtils;

    public BeanLookupCallGenerator(IOCContext context) {
        this.context = context;
        this.generationUtils = new GenerationUtils(context);
    }

    abstract Expression generate(ClassBuilder clazz,
                        InjectableVariableDefinition fieldPoint);

}
