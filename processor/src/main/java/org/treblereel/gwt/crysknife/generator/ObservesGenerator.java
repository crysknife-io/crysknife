package org.treblereel.gwt.crysknife.generator;

import java.util.function.Consumer;

import javax.enterprise.event.Observes;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.definition.ExecutableDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/5/19
 */
@Generator(priority = 1000)
public class ObservesGenerator extends IOCGenerator {

    @Override
    public void register(IOCContext iocContext) {
        iocContext.register(Observes.class, WiringElementType.PARAMETER, this);
    }

    @Override
    public void generateBeanFactory(ClassBuilder classBuilder, Definition definition) {
        if (definition instanceof ExecutableDefinition) {
            ExecutableDefinition methodDefinition = (ExecutableDefinition) definition;

            ExecutableElement method = methodDefinition.getExecutableElement();
            if (method.getParameters().size() > 1) {
                throw new Error("Method annotated with @Observes must contains only one param " + method.getEnclosingElement() + " " + method);
            }

            classBuilder.getClassCompilationUnit().addImport(Consumer.class);

            VariableElement parameter = method.getParameters().get(0);
            classBuilder.getClassCompilationUnit().addImport("javax.enterprise.event.Event_Factory");
            MethodCallExpr eventFactory = new MethodCallExpr(new NameExpr("Event_Factory").getNameAsExpression(), "get");
            MethodCallExpr getEventHandler = new MethodCallExpr(eventFactory, "get")
                    .addArgument(new FieldAccessExpr(new NameExpr(parameter.asType().toString()), "class"));

            Parameter argument = new Parameter();
            argument.setName("(event)");

            ExpressionStmt expressionStmt = new ExpressionStmt();
            VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();

            VariableDeclarator variableDeclarator = new VariableDeclarator();
            variableDeclarator.setName(parameter.getEnclosingElement().getSimpleName().toString());

            ClassOrInterfaceType consumerClassDecloration = new ClassOrInterfaceType().setName(Consumer.class.getCanonicalName());
            consumerClassDecloration.setTypeArguments(new ClassOrInterfaceType().setName(parameter.asType().toString()));
            variableDeclarator.setType(consumerClassDecloration);
            variableDeclarator.setInitializer("event -> this.instance." + method.getSimpleName().toString() + "(event)");
            variableDeclarationExpr.getVariables().add(variableDeclarator);
            expressionStmt.setExpression(variableDeclarationExpr);

            classBuilder.getGetMethodDeclaration()
                    .getBody()
                    .get()
                    .addAndGetStatement(expressionStmt);

            EnclosedExpr castToAbstractEventHandler = new EnclosedExpr(new CastExpr(new ClassOrInterfaceType().setName("org.treblereel.gwt.crysknife.client.internal.AbstractEventHandler"), getEventHandler));

            MethodCallExpr addSubscriber = new MethodCallExpr(castToAbstractEventHandler, "addSubscriber").addArgument(parameter.getEnclosingElement().getSimpleName().toString());

            classBuilder.getGetMethodDeclaration()
                    .getBody()
                    .get()
                    .addAndGetStatement(addSubscriber);
        }
    }
}
