/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.generator;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
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
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.client.BeanManager;
import io.crysknife.client.Interceptor;
import io.crysknife.client.Reflect;
import io.crysknife.client.internal.Factory;
import io.crysknife.client.internal.OnFieldAccessed;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;
import io.crysknife.generator.definition.Definition;
import io.crysknife.generator.definition.ExecutableDefinition;
import io.crysknife.generator.point.FieldPoint;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.Utils;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Provider;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/19
 */
public abstract class ScopedBeanGenerator extends BeanIOCGenerator {

  protected FieldAccessExpr instance;

  public ScopedBeanGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  protected Expression generateInstanceInitializer(ClassBuilder classBuilder,
      BeanDefinition definition) {

    instance = new FieldAccessExpr(new ThisExpr(), "instance");
    ObjectCreationExpr newInstance = generateNewInstanceCreationExpr(definition);

    // TODO refactoring
    if (definition.getConstructorInjectionPoint() != null) {
      for (FieldPoint argument : definition.getConstructorInjectionPoint().getArguments()) {
        generateFactoryFieldDeclaration(classBuilder, argument, "constructor");
        newInstance.addArgument(new MethodCallExpr(
            new MethodCallExpr(
                new FieldAccessExpr(new ThisExpr(), "_constructor_" + argument.getName()), "get"),
            "get"));
      }
    }

    definition.getFieldInjectionPoints().forEach(f -> {
      generateFactoryFieldDeclaration(classBuilder, f, "field");
    });

    Expression instanceFieldAssignExpr;
    if (iocContext.getGenerationContext().isGwt2() || iocContext.getGenerationContext().isJre()) {
      instanceFieldAssignExpr = newInstance;
    } else {
      FieldAccessExpr interceptor = new FieldAccessExpr(new ThisExpr(), "interceptor");

      ObjectCreationExpr interceptorCreationExpr = new ObjectCreationExpr();
      interceptorCreationExpr.setType(Interceptor.class.getSimpleName());
      interceptorCreationExpr.addArgument(newInstance);

      classBuilder.getGetMethodDeclaration().getBody().get().addAndGetStatement(
          new AssignExpr().setTarget(interceptor).setValue(interceptorCreationExpr));
      instanceFieldAssignExpr = new MethodCallExpr(interceptor, "getProxy");
    }

    return new AssignExpr().setTarget(instance).setValue(instanceFieldAssignExpr);
  }

  protected ObjectCreationExpr generateNewInstanceCreationExpr(BeanDefinition definition) {
    ObjectCreationExpr newInstance = new ObjectCreationExpr();
    return newInstance.setType(Utils.getSimpleClassName(definition.getType()));
  }

  protected void generateFactoryFieldDeclaration(ClassBuilder classBuilder, FieldPoint fieldPoint,
      String kind) {

    String varName = "_" + kind + "_" + fieldPoint.getName();
    ClassOrInterfaceType supplier =
        new ClassOrInterfaceType().setName(Supplier.class.getSimpleName());

    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(Instance.class.getSimpleName());
    type.setTypeArguments(
        new ClassOrInterfaceType().setName(fieldPoint.getType().getQualifiedName().toString()));
    supplier.setTypeArguments(type);

    classBuilder.addField(supplier, varName, Modifier.Keyword.PRIVATE);

  }

  @Override
  public Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint) {

    MethodCallExpr callForProducer = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
        .addArgument(new FieldAccessExpr(
            new NameExpr(fieldPoint.getType().getQualifiedName().toString()), "class"));

    generationUtils.maybeAddQualifiers(iocContext, callForProducer, fieldPoint);
    return callForProducer;
  }

  @Override
  public void generateBeanFactory(ClassBuilder clazz, Definition definition) {
    if (definition instanceof BeanDefinition) {

      BeanDefinition beanDefinition = (BeanDefinition) definition;

      checkContainsPostConstruct(beanDefinition);

      initClassBuilder(clazz, beanDefinition);

      if (!iocContext.getGenerationContext().isGwt2()
          && !iocContext.getGenerationContext().isJre()) {
        generateInterceptorFieldDeclaration(clazz);
      }

      generateInstanceGetMethodBuilder(clazz, beanDefinition);

      generateDependantFieldDeclaration(clazz, beanDefinition);

      generateInstanceGetFieldDecorators(clazz, beanDefinition);

      generateInstanceGetMethodDecorators(clazz, beanDefinition);

      generateInstanceGetMethodReturn(clazz, beanDefinition);

      write(clazz, beanDefinition, iocContext.getGenerationContext());
    }
  }

  // TODO this must be fixed
  protected void checkContainsPostConstruct(BeanDefinition beanDefinition) {
    TypeElement type = iocContext.getGenerationContext().getElements()
        .getTypeElement(Object.class.getCanonicalName());

    IOCContext.IOCGeneratorMeta meta = new IOCContext.IOCGeneratorMeta(
        PostConstruct.class.getCanonicalName(), type, WiringElementType.METHOD_DECORATOR);

    IOCGenerator generator = iocContext.getGenerators().get(meta).stream().findFirst().get();

    iocContext.getGenerationContext().getTypes().directSupertypes(beanDefinition.getType().asType())
        .stream()
        .map(e -> MoreElements.getAllMethods(MoreTypes.asTypeElement(e),
            iocContext.getGenerationContext().getTypes(),
            iocContext.getGenerationContext().getElements()))
        .flatMap(Collection::stream).filter(f -> f.getAnnotation(PostConstruct.class) != null)
        .forEach(f -> beanDefinition.addExecutableDefinition(generator,
            ExecutableDefinition.of(f, MoreElements.asType(f.getEnclosingElement()))));
  }

  public void initClassBuilder(ClassBuilder clazz, BeanDefinition beanDefinition) {
    clazz.getClassCompilationUnit().setPackageDeclaration(beanDefinition.getPackageName());
    clazz.getClassCompilationUnit().addImport(Factory.class);
    clazz.getClassCompilationUnit().addImport(Instance.class);
    clazz.getClassCompilationUnit().addImport(Provider.class);
    clazz.getClassCompilationUnit().addImport(OnFieldAccessed.class);
    clazz.getClassCompilationUnit().addImport(Reflect.class);
    clazz.getClassCompilationUnit().addImport(Supplier.class);
    clazz.getClassCompilationUnit().addImport(BeanManager.class);
    clazz.setClassName(beanDefinition.getClassFactoryName());

    ClassOrInterfaceType factory = new ClassOrInterfaceType();
    factory.setName("Factory<" + Utils.getSimpleClassName(beanDefinition.getType()) + ">");
    clazz.getImplementedTypes().add(factory);
  }

  private void generateInterceptorFieldDeclaration(ClassBuilder clazz) {
    clazz.getClassCompilationUnit().addImport(Interceptor.class);
    clazz.addField(Interceptor.class.getSimpleName(), "interceptor", Modifier.Keyword.PRIVATE);
  }

  public void generateInstanceGetMethodBuilder(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    MethodDeclaration getMethodDeclaration = classBuilder.addMethod("get", Modifier.Keyword.PUBLIC);

    getMethodDeclaration.addAnnotation(Override.class);
    getMethodDeclaration.setType(Utils.getSimpleClassName(classBuilder.beanDefinition.getType()));
    classBuilder.setGetMethodDeclaration(getMethodDeclaration);
  }

  public void generateDependantFieldDeclaration(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {

    classBuilder.addField(BeanManager.class.getSimpleName(), "beanManager",
        Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);

    ConstructorDeclaration constructorDeclaration =
        classBuilder.addConstructorDeclaration(Modifier.Keyword.PUBLIC);
    constructorDeclaration.addAndGetParameter(BeanManager.class, "beanManager");

    constructorDeclaration.getBody().addAndGetStatement(
        new AssignExpr().setTarget(new FieldAccessExpr(new ThisExpr(), "beanManager"))
            .setValue(new NameExpr("beanManager")));


    // TODO refactoring
    if (beanDefinition.getConstructorInjectionPoint() != null) {
      for (FieldPoint argument : beanDefinition.getConstructorInjectionPoint().getArguments()) {
        addFactoryFieldInitialization(classBuilder, argument, "constructor", beanDefinition);
      }
    }

    beanDefinition.getFieldInjectionPoints().forEach(f -> {
      addFactoryFieldInitialization(classBuilder, f, "field", beanDefinition);
    });



    if (!iocContext.getGenerationContext().isJre()) {
      beanDefinition.getFieldInjectionPoints().forEach(fieldPoint -> {
        Expression expr = getFieldAccessorExpression(classBuilder, beanDefinition, fieldPoint);
        classBuilder.getGetMethodDeclaration().getBody().get().addStatement(expr);
      });
    }
  }

  protected Expression getFieldAccessorExpression(ClassBuilder classBuilder,
      BeanDefinition beanDefinition, FieldPoint fieldPoint) {
    String varName = "_field_" + fieldPoint.getName();

    if (iocContext.getGenerationContext().isGwt2()) {
      return new MethodCallExpr(Utils.getSimpleClassName(classBuilder.beanDefinition.getType())
          + "Info." + fieldPoint.getName())
              .addArgument(new FieldAccessExpr(new ThisExpr(), "instance"))
              .addArgument(new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), varName), "get"));
    }

    FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(new ThisExpr(), "interceptor");

    MethodCallExpr reflect =
        new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
            .addArgument(beanDefinition.getClassName() + "Info." + fieldPoint.getName())
            .addArgument(new FieldAccessExpr(new ThisExpr(), "instance"));

    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(
        new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), varName), "get")));

    ObjectCreationExpr onFieldAccessedCreationExpr = new ObjectCreationExpr();
    onFieldAccessedCreationExpr.setType(OnFieldAccessed.class.getSimpleName());
    onFieldAccessedCreationExpr.addArgument(lambda);

    return new MethodCallExpr(fieldAccessExpr, "addGetPropertyInterceptor").addArgument(reflect)
        .addArgument(onFieldAccessedCreationExpr);
  }

  public void addFactoryFieldInitialization(ClassBuilder classBuilder, FieldPoint fieldPoint,
      String kind, BeanDefinition beanDefinition) {
    String varName = "_" + kind + "_" + fieldPoint.getName();

    Expression beanCall =
        iocContext.getBean(fieldPoint).generateBeanCall(iocContext, classBuilder, fieldPoint);

    FieldAccessExpr field = new FieldAccessExpr(new ThisExpr(), varName);

    LambdaExpr lambda = new LambdaExpr().setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(beanCall));

    AssignExpr assign = new AssignExpr().setTarget(field).setValue(lambda);

    classBuilder.addStatementToConstructor(assign);
  }

  private void generateInstanceGetFieldDecorators(ClassBuilder clazz,
      BeanDefinition beanDefinition) {
    beanDefinition.getFieldInjectionPoints().forEach(fi -> {
      fi.postActions.forEach(gen -> gen.generateBeanFactory(clazz, beanDefinition));
    });
  }

  private void generateInstanceGetMethodDecorators(ClassBuilder clazz,
      BeanDefinition beanDefinition) {
    beanDefinition.generateDecorators(clazz);
  }

  public void generateInstanceGetMethodReturn(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    classBuilder.getGetMethodDeclaration().getBody().get()
        .addStatement(new ReturnStmt(new FieldAccessExpr(new ThisExpr(), "instance")));
  }

}
