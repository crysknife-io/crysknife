package org.treblereel.gwt.crysknife.generator;

import java.util.function.Supplier;

import javax.inject.Provider;
import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.client.Instance;
import org.treblereel.gwt.crysknife.client.Interceptor;
import org.treblereel.gwt.crysknife.client.Reflect;
import org.treblereel.gwt.crysknife.client.internal.Factory;
import org.treblereel.gwt.crysknife.client.internal.OnFieldAccessed;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public abstract class ScopedBeanGenerator extends BeanIOCGenerator {

    protected FieldAccessExpr instance;

    public ScopedBeanGenerator(IOCContext iocContext) {
        super(iocContext);
    }

    @Override
    public void generateBeanFactory(ClassBuilder clazz, Definition definition) {
        if (definition instanceof BeanDefinition) {

            BeanDefinition beanDefinition = (BeanDefinition) definition;

            initClassBuilder(clazz, beanDefinition);

            generateInterceptorFieldDeclaration(clazz);

            generateInstanceGetMethodBuilder(clazz, beanDefinition);

            generateDependantFieldDeclaration(clazz, beanDefinition);

            generateInstanceGetMethodDecorators(clazz, beanDefinition);

            generateInstanceGetMethodReturn(clazz, beanDefinition);

            generateFactoryCreateMethod(clazz, beanDefinition);

            write(clazz,
                  beanDefinition,
                  iocContext.getGenerationContext());
        }
    }

    public void initClassBuilder(ClassBuilder clazz, BeanDefinition beanDefinition) {
        clazz.getClassCompilationUnit().setPackageDeclaration(beanDefinition.getPackageName());
        clazz.getClassCompilationUnit().addImport(Factory.class);
        clazz.getClassCompilationUnit().addImport(Provider.class);
        clazz.getClassCompilationUnit().addImport(OnFieldAccessed.class);
        clazz.getClassCompilationUnit().addImport(Reflect.class);
        clazz.setClassName(beanDefinition.getClassFactoryName());

        ClassOrInterfaceType factory = new ClassOrInterfaceType();
        factory.setName("Factory<" + beanDefinition.getClassName() + ">");
        clazz.getImplementedTypes().add(factory);
    }

    private void generateInterceptorFieldDeclaration(ClassBuilder clazz) {
        clazz.getClassCompilationUnit().addImport(Interceptor.class);
        clazz.addField(Interceptor.class.getSimpleName(), "interceptor", Modifier.Keyword.PRIVATE);
    }

    public void generateInstanceGetMethodBuilder(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        MethodDeclaration getMethodDeclaration = classBuilder.addMethod("get", Modifier.Keyword.PUBLIC);

        getMethodDeclaration.addAnnotation(Override.class);
        getMethodDeclaration.setType(classBuilder.beanDefinition.getClassName());
        classBuilder.setGetMethodDeclaration(getMethodDeclaration);
    }

    public void generateDependantFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.addConstructorDeclaration(Modifier.Keyword.PRIVATE);
        beanDefinition.getFieldInjectionPoints().forEach(fieldPoint -> {

            MethodCallExpr interceptor = getFieldAccessorExpression(classBuilder, beanDefinition, fieldPoint);
            classBuilder.getGetMethodDeclaration().getBody().get().addStatement(interceptor);
        });
    }

    private void generateInstanceGetMethodDecorators(ClassBuilder clazz, BeanDefinition beanDefinition) {
        beanDefinition.generateDecorators(clazz);
    }

    public void generateInstanceGetMethodReturn(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.getGetMethodDeclaration().getBody()
                .get().addStatement(new ReturnStmt(new FieldAccessExpr(new ThisExpr(), "instance")));
    }

    public void generateFactoryCreateMethod(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        MethodDeclaration methodDeclaration = classBuilder.addMethod("create", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
        methodDeclaration.setType(beanDefinition.getClassFactoryName());
        ObjectCreationExpr newInstance = new ObjectCreationExpr();
        newInstance.setType(new ClassOrInterfaceType().setName(beanDefinition.getClassFactoryName()));
        methodDeclaration.getBody().get().getStatements().add(new ReturnStmt(newInstance));
    }

    protected MethodCallExpr getFieldAccessorExpression(ClassBuilder classBuilder, BeanDefinition beanDefinition, FieldPoint fieldPoint) {
        FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(
                new ThisExpr(), "interceptor");

        MethodCallExpr reflect = new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                .addArgument(beanDefinition.getClassName() + "Info." + fieldPoint.getName()).addArgument(new FieldAccessExpr(
                        new ThisExpr(), "instance"));

        LambdaExpr lambda = new LambdaExpr();
        lambda.setEnclosingParameters(true);
        lambda.setBody(new ExpressionStmt(
                iocContext.getBeans()
                        .get(fieldPoint.getType())
                        .generateBeanCall(iocContext, classBuilder, fieldPoint))
        );

        ObjectCreationExpr onFieldAccessedCreationExpr = new ObjectCreationExpr();
        onFieldAccessedCreationExpr.setType(OnFieldAccessed.class.getSimpleName());
        onFieldAccessedCreationExpr.addArgument(lambda);

        return new MethodCallExpr(fieldAccessExpr, "addGetPropertyInterceptor")
                .addArgument(reflect)
                .addArgument(onFieldAccessedCreationExpr);
    }

    protected Expression generateInstanceInitializer(ClassBuilder classBuilder, BeanDefinition definition) {

        instance = new FieldAccessExpr(new ThisExpr(), "instance");
        FieldAccessExpr interceptor = new FieldAccessExpr(new ThisExpr(), "interceptor");

        ObjectCreationExpr newInstance = generateNewInstanceCreationExpr(definition);

        //TODO refactoring
        if (definition.getConstructorInjectionPoint() != null) {
            classBuilder.addConstructorDeclaration(Modifier.Keyword.PRIVATE);
            for (FieldPoint argument : definition.getConstructorInjectionPoint().getArguments()) {
                generateFactoryFieldDeclaration(classBuilder, argument.getType());
                newInstance.addArgument(iocContext.getBeans().get(argument.getType()).generateBeanCall(iocContext, classBuilder, argument));
            }
        }

        ObjectCreationExpr interceptorCreationExpr = new ObjectCreationExpr();
        interceptorCreationExpr.setType(Interceptor.class.getSimpleName());
        interceptorCreationExpr.addArgument(newInstance);

        classBuilder.getGetMethodDeclaration().getBody()
                .get()
                .addAndGetStatement(new AssignExpr().setTarget(interceptor).setValue(interceptorCreationExpr));

        return new AssignExpr().setTarget(instance).setValue(new MethodCallExpr(interceptor, "getProxy"));
    }

    protected ObjectCreationExpr generateNewInstanceCreationExpr(BeanDefinition definition) {
        ObjectCreationExpr newInstance = new ObjectCreationExpr();
        return newInstance.setType(definition.getClassName());
    }

    @Override
    public Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        generateFactoryFieldDeclaration(clazz, beanDefinition);
        generateFactoryConstructorDepsBuilder(clazz, beanDefinition);
        TypeElement point = fieldPoint.isNamed() ? iocContext.getQualifiers()
                .get(fieldPoint.getType())
                .get(fieldPoint.getNamed())
                .getType() : fieldPoint.getType();
        return new MethodCallExpr(new MethodCallExpr(new NameExpr(Utils.toVariableName(point)), "get"), "get");
    }

    protected void generateFactoryFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        generateFactoryFieldDeclaration(classBuilder, beanDefinition.getType());
    }

    public void generateFactoryConstructorDepsBuilder(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.BeanManagerImpl");
        classBuilder.getClassCompilationUnit().addImport(Instance.class.getCanonicalName());
        classBuilder.getClassCompilationUnit().addImport(Supplier.class.getCanonicalName());
        addFactoryFieldInitialization(classBuilder, beanDefinition);
    }

    protected void generateFactoryFieldDeclaration(ClassBuilder classBuilder, FieldPoint fieldPoint) {
        generateFactoryFieldDeclaration(classBuilder, fieldPoint.getType());
    }

    protected void generateFactoryFieldDeclaration(ClassBuilder classBuilder, TypeElement typeElement) {
        String varName = Utils.toVariableName(typeElement);
        ClassOrInterfaceType supplier = new ClassOrInterfaceType().setName(Supplier.class.getSimpleName());

        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName(Instance.class.getSimpleName());
        type.setTypeArguments(new ClassOrInterfaceType().setName(typeElement.getQualifiedName().toString()));
        supplier.setTypeArguments(type);
        classBuilder.addField(supplier, varName, Modifier.Keyword.PRIVATE);
    }

    public void addFactoryFieldInitialization(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        ClassOrInterfaceType beanManager = new ClassOrInterfaceType().setName(getFactoryVariableName());
        MethodCallExpr callForBeanManagerImpl = new MethodCallExpr(beanManager.getNameAsExpression(), "get");

        MethodCallExpr callForProducer = new MethodCallExpr(callForBeanManagerImpl, "lookupBean")
                .addArgument(new FieldAccessExpr(new NameExpr(beanDefinition.getQualifiedName()), "class"));
        FieldAccessExpr field = new FieldAccessExpr(new ThisExpr(), varName);

        LambdaExpr lambda = new LambdaExpr().setEnclosingParameters(true);
        lambda.setBody(new ExpressionStmt(callForProducer));

        AssignExpr assign = new AssignExpr().setTarget(field).setValue(lambda);

        classBuilder.addStatementToConstructor(assign);
    }

    public String getFactoryVariableName() {
        return "BeanManagerImpl";
    }
}
