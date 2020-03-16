package org.treblereel.gwt.crysknife.generator;

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.client.Instance;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.ProducerDefinition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
@Generator(priority = 500)
public class ProducesGenerator extends ScopedBeanGenerator {

    private static final String BEAN_MANAGER_IMPL = "org.treblereel.gwt.crysknife.client.BeanManagerImpl";

    public ProducesGenerator(IOCContext iocContext) {
        super(iocContext);
    }

    @Override
    public void register() {
        iocContext.register(Produces.class, WiringElementType.PRODUCER_ELEMENT, this);
    }

    @Override
    public void generateDependantFieldDeclaration(ClassBuilder builder, BeanDefinition definition) {
        if (definition instanceof ProducerDefinition) {
            ProducerDefinition producesDefinition = (ProducerDefinition) definition;

            builder.getClassCompilationUnit().addImport(Instance.class);
            builder.getClassCompilationUnit().addImport(Supplier.class);
            builder.getClassCompilationUnit().addImport(producesDefinition.getInstance().getQualifiedName().toString());
            builder.getClassCompilationUnit().addImport(producesDefinition.getMethod().getReturnType().toString());

            TypeElement instance = producesDefinition.getInstance();

            Expression call = getBeanManagerCallExpr(instance);

            ClassOrInterfaceType supplier = new ClassOrInterfaceType().setName(Supplier.class.getSimpleName());

            ClassOrInterfaceType type = new ClassOrInterfaceType();
            type.setName(Instance.class.getSimpleName());
            type.setTypeArguments(new ClassOrInterfaceType().setName(definition.getType().getQualifiedName().toString()));
            supplier.setTypeArguments(type);

            builder.addFieldWithInitializer(supplier,
                                            "producer",
                                            call,
                                            Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);
        }
    }

    private Expression getBeanManagerCallExpr(TypeElement instance) {
        LambdaExpr lambda = new LambdaExpr();
        lambda.setEnclosingParameters(true);
        lambda.setBody(new ExpressionStmt(new MethodCallExpr(
                new MethodCallExpr(
                        new ClassOrInterfaceType().setName(BEAN_MANAGER_IMPL)
                                .getNameAsExpression(), "get"), "lookupBean")
                                                  .addArgument(instance.getQualifiedName().toString() + ".class")));

        return lambda;
    }

    @Override
    public void generateInstanceGetMethodReturn(ClassBuilder builder, BeanDefinition definition) {
        if (definition instanceof ProducerDefinition) {
            ExecutableElement method = ((ProducerDefinition) definition).getMethod();
            if (isSingleton(method)) {
                builder.addField(MoreTypes.asTypeElement(method.getReturnType()).getSimpleName().toString(), "holder", Modifier.Keyword.PRIVATE);

                IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(new FieldAccessExpr(
                        new ThisExpr(), "holder"), new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));

                ifStmt.setThenStmt(new BlockStmt().addAndGetStatement(new AssignExpr().setTarget(new FieldAccessExpr(
                        new ThisExpr(), "holder")).setValue(getMethodCallExpr(builder, (ProducerDefinition) definition))));

                builder.getGetMethodDeclaration().getBody()
                        .get()
                        .addAndGetStatement(ifStmt);

                builder.getGetMethodDeclaration().getBody()
                        .get()
                        .addAndGetStatement(new ReturnStmt(
                                new FieldAccessExpr(
                                        new ThisExpr(), "holder")));
            } else {
                builder.getGetMethodDeclaration().getBody().get().addAndGetStatement(
                        new ReturnStmt(getMethodCallExpr(builder, (ProducerDefinition) definition)));
            }
        }
    }

    @Override
    public Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        generateFactoryConstructorDepsBuilder(clazz, beanDefinition);
        TypeElement point = fieldPoint.isNamed() ? iocContext.getQualifiers()
                .get(fieldPoint.getType())
                .get(fieldPoint.getNamed())
                .getType() : fieldPoint.getType();
        return new MethodCallExpr(new MethodCallExpr(new NameExpr(Utils.toVariableName(point)), "get"), "get");
    }

    private boolean isSingleton(ExecutableElement method) {
        return method.getAnnotation(ApplicationScoped.class) != null
                || method.getAnnotation(Singleton.class) != null;
    }

    private MethodCallExpr getMethodCallExpr(ClassBuilder builder, ProducerDefinition definition) {
        CastExpr onCast = new CastExpr(
                new ClassOrInterfaceType().setName(definition.getInstance().getSimpleName().toString()),
                new MethodCallExpr(new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), "producer"), "get"),"get"));

        return new MethodCallExpr(new EnclosedExpr(onCast), definition.getMethod()
                .getSimpleName().toString());
    }
}
