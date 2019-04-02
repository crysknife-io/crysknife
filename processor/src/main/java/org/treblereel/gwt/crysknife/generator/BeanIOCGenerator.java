package org.treblereel.gwt.crysknife.generator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;

import javax.annotation.processing.FilerException;
import javax.inject.Provider;
import javax.tools.JavaFileObject;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/4/19
 */
public abstract class BeanIOCGenerator extends IOCGenerator {

    protected IOCContext iocContext;

    public void generate(ClassBuilder clazz, Definition definition) {
        if (definition instanceof BeanDefinition) {
            BeanDefinition beanDefinition = (BeanDefinition) definition;

            initClassBuilder(clazz, beanDefinition);

            generateDependantFieldDeclaration(clazz, beanDefinition);

            generateInstanceGetMethodBuilder(clazz, beanDefinition);
            generateInstanceGetMethodInitializer(clazz, beanDefinition);

            generatePostInstanceConstructorInitializer(clazz, beanDefinition);
            generateInstanceGetMethodReturn(clazz, beanDefinition);

            generateFactoryConstructorDepsBuilder(clazz, beanDefinition);
            generateFactoryCreateMethod(clazz, beanDefinition);
            generateFactoryMethods(clazz, beanDefinition);

            write(clazz, beanDefinition, iocContext.getGenerationContext());
        }
    }

    public void write(ClassBuilder clazz, BeanDefinition beanDefinition, GenerationContext context) {
        try {
            String fileName = Utils.getQualifiedFactoryName(beanDefinition.getType());
            String source = clazz.toSourceCode();
            build(fileName, source, context);
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
        clazz.getClassCompilationUnit().addImport(beanDefinition.getQualifiedName());
        clazz.setClassName(beanDefinition.getClassFactoryName());

        ClassOrInterfaceType factory = new ClassOrInterfaceType();
        factory.setName("Factory<" + beanDefinition.getClassName() + ">");
        clazz.getClassDeclaration().getImplementedTypes().add(factory);
    }

    public void generatePostInstanceConstructorInitializer(ClassBuilder clazz, BeanDefinition beanDefinition) {
        Comparator<IOCGenerator> comparator = Comparator.comparing(h -> Integer.valueOf(h.getClass().getAnnotation(Generator.class).priority()));

        beanDefinition.getExecutableDefinitions().keySet().stream().sorted(comparator).forEach(k -> {
            beanDefinition.getExecutableDefinitions().get(k).forEach(executable -> {
                executable.generate(clazz); //TODO
                k.generate(clazz, executable);
            });
        });

/*                beanDefinition.getExecutableDefinitions().values().stream().sorted(exec -> exec.get)
        forEach((k, executable) -> {
            executable.forEach(exec -> {
                //k.generate(clazz, exec);
                exec.generate(clazz); //TODO
            });
        });*/
    }

    public void generateInstanceGetMethodReturn(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.getGetMethodDeclaration().getBody()
                .get().addAndGetStatement(new ReturnStmt(new FieldAccessExpr(new ThisExpr(), "instance")));
    }

    public void generateInstanceGetMethodInitializer(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        beanDefinition.getFieldInjectionPoints().forEach(injection -> {
            classBuilder.getGetMethodDeclaration().getBody().get().addAndGetStatement(injection.generate());
        });
    }

    public void generateDependantFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.addConstructorDeclaration(Modifier.Keyword.PRIVATE);
        beanDefinition.getDependsOn().forEach(on -> generateFactoryFieldDeclaration(classBuilder, on));
    }

    protected void generateFactoryFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        beanDefinition.generateFactoryFieldDeclaration(classBuilder, beanDefinition);
    }

    public void generateFactoryCreateMethod(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        MethodDeclaration methodDeclaration = classBuilder.getClassDeclaration().addMethod("create", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
        methodDeclaration.setType(beanDefinition.getClassFactoryName());
        ObjectCreationExpr newInstance = new ObjectCreationExpr();
        newInstance.setType(new ClassOrInterfaceType().setName(beanDefinition.getClassFactoryName()));
        methodDeclaration.getBody().get().getStatements().add(new ReturnStmt(newInstance));
    }

    public void generateInstanceGetMethodBuilder(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        MethodDeclaration getMethodDeclaration = classBuilder.getClassDeclaration()
                .addMethod("get", Modifier.Keyword.PUBLIC);

        getMethodDeclaration.addAnnotation(Override.class);
        getMethodDeclaration.setType(classBuilder.beanDefinition.getClassName());
        classBuilder.setGetMethodDeclaration(getMethodDeclaration);
    }

    public void generateFactoryConstructorDepsBuilder(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.BeanManagerImpl");
        classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.Instance");

        beanDefinition.getDependsOn().forEach(on -> {
            on.addFactoryFieldInitialization(classBuilder, on);
        });
    }

    public void generateFactoryMethods(ClassBuilder classBuilder, BeanDefinition beanDefinition) {

    }

    public String getFactoryVariableName() {
        return "BeanManagerImpl";
    }

    public void addFactoryFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName("org.treblereel.gwt.crysknife.client.Instance");
        type.setTypeArguments(new ClassOrInterfaceType().setName(beanDefinition.getQualifiedName()));

        Parameter param = new Parameter();
        param.setName(varName);
        param.setType(type);

        classBuilder.getClassDeclaration().addField(type, varName, Modifier.Keyword.FINAL, Modifier.Keyword.PRIVATE);
    }

    public void addFactoryFieldInitialization(ClassBuilder classBuilder, BeanDefinition beanDefinition) {

        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        ClassOrInterfaceType beanManager = new ClassOrInterfaceType();
        beanManager.setName(beanDefinition.getFactoryVariableName());
        MethodCallExpr callForBeanManagerImpl = new MethodCallExpr(beanManager.getNameAsExpression(), "get");
        MethodCallExpr callForProducer = new MethodCallExpr(callForBeanManagerImpl, "lookupBean")
                .addArgument(new StringLiteralExpr(beanDefinition.getQualifiedName()));
        ThisExpr clazz = new ThisExpr();
        FieldAccessExpr field = new FieldAccessExpr(clazz, varName);
        AssignExpr assign = new AssignExpr().setTarget(field).setValue(callForProducer);
        classBuilder.getConstructorDeclaration().getBody().addStatement(assign);
    }
}