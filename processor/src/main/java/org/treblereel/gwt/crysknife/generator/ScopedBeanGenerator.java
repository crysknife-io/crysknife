package org.treblereel.gwt.crysknife.generator;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.processing.FilerException;
import javax.inject.Provider;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

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
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
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
    protected IOCContext iocContext;

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

    private void generateInterceptorFieldDeclaration(ClassBuilder clazz) {
        clazz.getClassCompilationUnit().addImport(Interceptor.class);
        clazz.addField(Interceptor.class.getSimpleName(), "interceptor", Modifier.Keyword.PRIVATE);
    }

    private void generateInstanceGetMethodDecorators(ClassBuilder clazz, BeanDefinition beanDefinition) {
        beanDefinition.generateDecorators(clazz);
    }

    public void write(ClassBuilder clazz, BeanDefinition beanDefinition, GenerationContext context) {
        try {
            String fileName = Utils.getQualifiedFactoryName(beanDefinition.getType());
            String source = clazz.toSourceCode();
            build(fileName, source, context);
        } catch (javax.annotation.processing.FilerException e1) {
            context.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.NOTE, e1.getMessage());
        } catch (IOException e1) {
            throw new Error(e1);
        }
    }

    protected void build(String fileName, String source, GenerationContext context) throws IOException {
        JavaFileObject builderFile = context.getProcessingEnvironment().getFiler()
                .createSourceFile(fileName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.append(source);
        } catch (FilerException e) {
            throw new Error(e);
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

    public void generateInstanceGetMethodReturn(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.getGetMethodDeclaration().getBody()
                .get().addStatement(new ReturnStmt(new FieldAccessExpr(new ThisExpr(), "instance")));
    }

    public void generateDependantFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.addConstructorDeclaration(Modifier.Keyword.PRIVATE);
        beanDefinition.getFieldInjectionPoints().forEach(fieldPoint -> {
            MethodCallExpr interceptor = getFieldAccessorExpression(classBuilder, beanDefinition, fieldPoint);
            classBuilder.getGetMethodDeclaration().getBody().get().addStatement(interceptor);
        });
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

    protected void generateFactoryFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName(Instance.class.getCanonicalName());
        type.setTypeArguments(new ClassOrInterfaceType().setName(beanDefinition.getQualifiedName()));

        classBuilder.addField(type, varName, Modifier.Keyword.FINAL, Modifier.Keyword.PRIVATE);
    }

    public void generateFactoryCreateMethod(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        MethodDeclaration methodDeclaration = classBuilder.addMethod("create", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
        methodDeclaration.setType(beanDefinition.getClassFactoryName());
        ObjectCreationExpr newInstance = new ObjectCreationExpr();
        newInstance.setType(new ClassOrInterfaceType().setName(beanDefinition.getClassFactoryName()));
        methodDeclaration.getBody().get().getStatements().add(new ReturnStmt(newInstance));
    }

    public void generateInstanceGetMethodBuilder(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        MethodDeclaration getMethodDeclaration = classBuilder.addMethod("get", Modifier.Keyword.PUBLIC);

        getMethodDeclaration.addAnnotation(Override.class);
        getMethodDeclaration.setType(classBuilder.beanDefinition.getClassName());
        classBuilder.setGetMethodDeclaration(getMethodDeclaration);
    }

    public void generateFactoryConstructorDepsBuilder(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.BeanManagerImpl");
        classBuilder.getClassCompilationUnit().addImport(Instance.class.getCanonicalName());
        addFactoryFieldInitialization(classBuilder, beanDefinition);
    }

    public String getFactoryVariableName() {
        return "BeanManagerImpl";
    }

    public void addFactoryFieldInitialization(ClassBuilder classBuilder, BeanDefinition beanDefinition) {

        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        ClassOrInterfaceType beanManager = new ClassOrInterfaceType();
        beanManager.setName(getFactoryVariableName());

        MethodCallExpr callForBeanManagerImpl = new MethodCallExpr(beanManager.getNameAsExpression(), "get");
        MethodCallExpr callForProducer = new MethodCallExpr(callForBeanManagerImpl, "lookupBean")
                .addArgument(new FieldAccessExpr(new NameExpr(beanDefinition.getQualifiedName()), "class"));
        FieldAccessExpr field = new FieldAccessExpr(new ThisExpr(), varName);
        AssignExpr assign = new AssignExpr().setTarget(field).setValue(callForProducer);
        classBuilder.addStatementToConstructor(assign);
    }

    protected Expression generateInstanceInitializer(ClassBuilder classBuilder, BeanDefinition definition) {

        instance = new FieldAccessExpr(new ThisExpr(), "instance");
        FieldAccessExpr interceptor = new FieldAccessExpr(new ThisExpr(), "interceptor");

        ObjectCreationExpr newInstance = new ObjectCreationExpr();
        newInstance.setType(definition.getClassName());

        if (definition.getConstructorInjectionPoint() != null) {
            classBuilder.addConstructorDeclaration(Modifier.Keyword.PRIVATE);
            for (FieldPoint argument : definition.getConstructorInjectionPoint().getArguments()) {
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

    @Override
    public Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        generateFactoryFieldDeclaration(clazz, beanDefinition);
        generateFactoryConstructorDepsBuilder(clazz, beanDefinition);
        return new MethodCallExpr(new NameExpr(Utils.toVariableName(fieldPoint.getType())), "get");
    }
}
