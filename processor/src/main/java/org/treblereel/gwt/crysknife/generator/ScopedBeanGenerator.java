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
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.point.ConstructorPoint;
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
        clazz.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.internal.Factory");
        clazz.getClassCompilationUnit().addImport(Provider.class);
        clazz.setClassName(beanDefinition.getClassFactoryName());

        ClassOrInterfaceType factory = new ClassOrInterfaceType();
        factory.setName("Factory<" + beanDefinition.getClassName() + ">");
        clazz.getImplementedTypes().add(factory);
    }

    public void generateInstanceGetMethodReturn(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.getGetMethodDeclaration().getBody()
                .get().addStatement(new ReturnStmt(new FieldAccessExpr(new ThisExpr(), "instance")));
    }

    public void generateInstanceGetMethodInitializer(ClassBuilder classBuilder, FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        ThisExpr clazz = new ThisExpr();
        FieldAccessExpr instance = new FieldAccessExpr(clazz, "instance");
        FieldAccessExpr field = new FieldAccessExpr(instance, fieldPoint.getName());
        MethodCallExpr call;
        if (MoreTypes.asTypeElement(fieldPoint.getField().asType()).getTypeParameters().size() > 0) {
            MethodCallExpr instanceCall = new MethodCallExpr(new NameExpr(Utils.toVariableName(fieldPoint.getType())), "get");
            call = new MethodCallExpr(instanceCall, "get");
            MoreTypes.asDeclared(fieldPoint.getField().asType()).getTypeArguments().forEach(tp -> {
                //TODO
                call.addArgument(new FieldAccessExpr(new NameExpr(tp.toString()), "class"));
            });
        } else {
            call = new MethodCallExpr(new NameExpr(Utils.toVariableName(fieldPoint.getType())), "get");
        }

        classBuilder.getGetMethodDeclaration().getBody().get().addStatement(new AssignExpr().setTarget(field).setValue(call));
    }

    public void generateDependantFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.addConstructorDeclaration(Modifier.Keyword.PRIVATE);
        beanDefinition.getFieldInjectionPoints().forEach(fieldPoint -> {
            FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(new FieldAccessExpr(
                    new ThisExpr(), "instance"), fieldPoint.getField().getSimpleName().toString());

            classBuilder.getGetMethodDeclaration().getBody().get().addStatement(
                    new AssignExpr().setTarget(fieldAccessExpr)
                            .setValue(iocContext.getBeans()
                                              .get(fieldPoint.getType())
                                              .generateBeanCall(classBuilder, fieldPoint)));
        });
    }

    protected void generateFactoryFieldDeclaration(ClassBuilder classBuilder, FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName("org.treblereel.gwt.crysknife.client.Instance");
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
        classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.Instance");
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

        if (definition.getConstructorInjectionPoint() == null) {
            instance = new FieldAccessExpr(new ThisExpr(), "instance");
            ObjectCreationExpr newInstance = new ObjectCreationExpr();
            newInstance.setType(definition.getClassName());
            return new AssignExpr().setTarget(instance).setValue(newInstance);
        } else {
            return generateInstanceConstructorInjectionPoint(classBuilder, definition.getConstructorInjectionPoint());
        }
    }

    protected Expression generateInstanceConstructorInjectionPoint(ClassBuilder classBuilder, ConstructorPoint point) {
        classBuilder.addConstructorDeclaration(Modifier.Keyword.PRIVATE);

        FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
        ObjectCreationExpr newInstance = new ObjectCreationExpr();
        newInstance.setType(point.getType().getSimpleName().toString());

        for (FieldPoint argument : point.getArguments()) {
            newInstance.addArgument(iocContext.getBeans().get(argument.getType()).generateBeanCall(classBuilder, argument));
        }
        return new AssignExpr().setTarget(instance).setValue(newInstance);
    }

    @Override
    public Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        generateFactoryFieldDeclaration(clazz, fieldPoint, beanDefinition);
        //generateInstanceGetMethodInitializer(clazz, fieldPoint, beanDefinition);
        generateFactoryConstructorDepsBuilder(clazz, beanDefinition);

        return new MethodCallExpr(new NameExpr(Utils.toVariableName(fieldPoint.getType())), "get");
    }
}
