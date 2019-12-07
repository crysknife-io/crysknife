package org.treblereel.gwt.crysknife.generator;

import java.io.IOException;

import javax.inject.Provider;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/5/19
 */
@Generator(priority = 100000)
public class BootstrapperGenerator extends ScopedBeanGenerator {

    private String BOOTSTRAP_EXTENSION = "Bootstrap";

    @Override
    public void register(IOCContext iocContext) {
        iocContext.register(Application.class, WiringElementType.DEPENDENT_BEAN, this);
        this.iocContext = iocContext;
    }

    @Override
    public void generateBeanFactory(ClassBuilder clazz, Definition definition) {
        super.generateBeanFactory(clazz, definition);
    }

    @Override
    public void generateDependantFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.addConstructorDeclaration();
        Parameter arg = new Parameter();
        arg.setName("application");
        arg.setType(beanDefinition.getType().getSimpleName().toString());

        classBuilder.addParametersToConstructor(arg);

        beanDefinition.getFieldInjectionPoints().forEach(fieldPoint -> iocContext.getBeans()
                .get(fieldPoint.getType())
                .generateBeanCall(iocContext, classBuilder, fieldPoint));

        AssignExpr assign = new AssignExpr().setTarget(new FieldAccessExpr(new ThisExpr(), "instance"))
                .setValue(new NameExpr("application"));
        classBuilder.addStatementToConstructor(assign);
    }

    protected void generateFactoryFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName("org.treblereel.gwt.crysknife.client.Instance");
        type.setTypeArguments(new ClassOrInterfaceType().setName(beanDefinition.getQualifiedName()));

        classBuilder.addField(type, varName, Modifier.Keyword.FINAL, Modifier.Keyword.PRIVATE);
    }

    @Override
    public void initClassBuilder(ClassBuilder clazz, BeanDefinition beanDefinition) {
        clazz.getClassCompilationUnit().setPackageDeclaration(beanDefinition.getPackageName());
        clazz.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.internal.Factory");
        clazz.getClassCompilationUnit().addImport(Provider.class);
        clazz.getClassCompilationUnit().addImport(beanDefinition.getQualifiedName());
        clazz.setClassName(beanDefinition.getType().getSimpleName().toString() + BOOTSTRAP_EXTENSION);

        clazz.addField(beanDefinition.getClassName(), "instance", Modifier.Keyword.PRIVATE);
    }

    @Override
    public void generateInstanceGetMethodBuilder(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.addConstructorDeclaration();

        MethodDeclaration getMethodDeclaration = classBuilder.addMethod("initialize");
        classBuilder.setGetMethodDeclaration(getMethodDeclaration);

        beanDefinition.getFieldInjectionPoints().forEach(fieldPoint -> {
            FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(new FieldAccessExpr(
                    new ThisExpr(), "instance"), fieldPoint.getField().getSimpleName().toString());
            classBuilder.getGetMethodDeclaration().getBody().get().addStatement(
                    new AssignExpr().setTarget(fieldAccessExpr)
                            .setValue(iocContext.getBean(fieldPoint.getType())
                                              .generateBeanCall(iocContext, classBuilder, fieldPoint)));
        });
    }

    @Override
    public void generateInstanceGetMethodReturn(ClassBuilder classBuilder, BeanDefinition beanDefinition) {

    }

    @Override
    public void generateFactoryCreateMethod(ClassBuilder classBuilder, BeanDefinition beanDefinition) {

    }

    @Override
    public void write(ClassBuilder clazz, BeanDefinition beanDefinition, GenerationContext context) {
        try {
            String fileName = Utils.getQualifiedName(beanDefinition.getType()) + BOOTSTRAP_EXTENSION;
            String source = clazz.toSourceCode();
            build(fileName, source, context);
        } catch (IOException e1) {
            throw new Error(e1);
        }
    }
}