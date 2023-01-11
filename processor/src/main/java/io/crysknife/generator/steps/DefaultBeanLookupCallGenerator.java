package io.crysknife.generator.steps;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;

public class DefaultBeanLookupCallGenerator extends BeanLookupCallGenerator {

    public DefaultBeanLookupCallGenerator(IOCContext context) {
        super(context);
    }

    @Override
    public Expression generate(ClassBuilder clazz, InjectableVariableDefinition fieldPoint) {
        String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);
        MethodCallExpr callForProducer = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                .addArgument(new FieldAccessExpr(new NameExpr(typeQualifiedName), "class"));
        return callForProducer;
    }
}
