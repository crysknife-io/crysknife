package org.treblereel.gwt.crysknife.generator;

import java.io.IOException;

import javax.inject.Provider;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.Instance;
import org.treblereel.gwt.crysknife.client.Interceptor;
import org.treblereel.gwt.crysknife.client.Reflect;
import org.treblereel.gwt.crysknife.client.internal.Factory;
import org.treblereel.gwt.crysknife.client.internal.OnFieldAccessed;
import org.treblereel.gwt.crysknife.exception.GenerationException;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/5/19
 */
@Generator(priority = 100000)
public class BootstrapperGenerator extends ScopedBeanGenerator {

  private String BOOTSTRAP_EXTENSION = "Bootstrap";

  public BootstrapperGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Application.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generateBeanFactory(ClassBuilder clazz, Definition definition) {
    super.generateBeanFactory(clazz, definition);
  }

  @Override
  public void initClassBuilder(ClassBuilder clazz, BeanDefinition beanDefinition) {
    clazz.getClassCompilationUnit().setPackageDeclaration(beanDefinition.getPackageName());
    clazz.getClassCompilationUnit().addImport(beanDefinition.getQualifiedName());

    if (!iocContext.getGenerationContext().isGwt2()) {
      clazz.getClassCompilationUnit().addImport(OnFieldAccessed.class);
      clazz.getClassCompilationUnit().addImport(Reflect.class);
      clazz.getClassCompilationUnit().addImport(Factory.class);
      clazz.getClassCompilationUnit().addImport(Provider.class);
    }

    clazz.setClassName(beanDefinition.getType().getSimpleName().toString() + BOOTSTRAP_EXTENSION);

    clazz.addField(beanDefinition.getClassName(), "instance", Modifier.Keyword.PRIVATE);
  }

  @Override
  public void generateInstanceGetMethodBuilder(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    classBuilder.addConstructorDeclaration();

    MethodDeclaration getMethodDeclaration = classBuilder.addMethod("initialize");
    classBuilder.setGetMethodDeclaration(getMethodDeclaration);

    if (!iocContext.getGenerationContext().isGwt2() && !iocContext.getGenerationContext().isJre()) {
      ObjectCreationExpr interceptorCreationExpr = new ObjectCreationExpr();
      interceptorCreationExpr.setType(Interceptor.class.getSimpleName());
      interceptorCreationExpr.addArgument(new NameExpr("instance"));

      classBuilder.getGetMethodDeclaration().getBody().get().addAndGetStatement(new AssignExpr()
          .setTarget(new NameExpr("interceptor")).setValue(interceptorCreationExpr));

      classBuilder.getGetMethodDeclaration().getBody().get()
          .addAndGetStatement(new AssignExpr().setTarget(new NameExpr("instance"))
              .setValue(new MethodCallExpr(new NameExpr("interceptor"), "getProxy")));
    }
    if (!iocContext.getGenerationContext().isJre()) {
      beanDefinition.getFieldInjectionPoints()
          .forEach(fieldPoint -> classBuilder.getGetMethodDeclaration().getBody().get()
              .addStatement(getFieldAccessorExpression(classBuilder, beanDefinition, fieldPoint)));
    }
  }

  @Override
  public void generateDependantFieldDeclaration(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    classBuilder.addConstructorDeclaration();
    Parameter arg = new Parameter();
    arg.setName("application");
    arg.setType(beanDefinition.getType().getSimpleName().toString());

    classBuilder.addParametersToConstructor(arg);

    beanDefinition.getFieldInjectionPoints().forEach(fieldPoint -> iocContext.getBeans()
        .get(fieldPoint.getType()).generateBeanCall(iocContext, classBuilder, fieldPoint));

    AssignExpr assign = new AssignExpr().setTarget(new FieldAccessExpr(new ThisExpr(), "instance"))
        .setValue(new NameExpr("application"));
    classBuilder.addStatementToConstructor(assign);
  }

  @Override
  public void generateInstanceGetMethodReturn(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {

  }

  @Override
  public void generateFactoryCreateMethod(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {

  }

  protected void generateFactoryFieldDeclaration(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(Instance.class.getCanonicalName());
    type.setTypeArguments(new ClassOrInterfaceType().setName(beanDefinition.getQualifiedName()));

    classBuilder.addField(type, varName, Modifier.Keyword.FINAL, Modifier.Keyword.PRIVATE);
  }

  @Override
  public void write(ClassBuilder clazz, BeanDefinition beanDefinition, GenerationContext context) {
    try {
      String fileName = Utils.getQualifiedName(beanDefinition.getType()) + BOOTSTRAP_EXTENSION;
      String source = clazz.toSourceCode();
      build(fileName, source, context);
    } catch (IOException e1) {
      throw new GenerationException(e1);
    }
  }
}
