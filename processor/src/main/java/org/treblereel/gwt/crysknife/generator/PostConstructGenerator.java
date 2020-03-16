package org.treblereel.gwt.crysknife.generator;

import javax.annotation.PostConstruct;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.definition.ExecutableDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
@Generator(priority = 100000)
public class PostConstructGenerator extends IOCGenerator {

    public PostConstructGenerator(IOCContext iocContext) {
        super(iocContext);
    }

    @Override
    public void register() {
        iocContext.register(PostConstruct.class, WiringElementType.METHOD_DECORATOR, this);
    }

    public void generateBeanFactory(ClassBuilder builder, Definition definition) {
        if (definition instanceof ExecutableDefinition) {
            ExecutableDefinition postConstract = (ExecutableDefinition) definition;
            FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
            MethodCallExpr method = new MethodCallExpr(instance,
                                                       postConstract.getExecutableElement().getSimpleName().toString());
            builder.getGetMethodDeclaration().getBody().get().addAndGetStatement(method);
        }
    }
}
