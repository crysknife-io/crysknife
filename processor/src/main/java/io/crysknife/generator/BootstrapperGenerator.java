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
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Application;
import io.crysknife.annotation.Generator;
import io.crysknife.client.BeanManager;
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.Interceptor;
import io.crysknife.client.Reflect;
import io.crysknife.client.SyncBeanDef;
import io.crysknife.client.internal.BeanFactory;
import io.crysknife.client.internal.OnFieldAccessed;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.GenerationContext;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.Utils;

import javax.inject.Provider;
import java.io.IOException;
import java.util.function.Supplier;

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
  public void generate(ClassBuilder clazz, BeanDefinition definition) {
    super.generate(clazz, definition);
  }

  @Override
  public void initClassBuilder(ClassBuilder clazz, BeanDefinition beanDefinition) {
    String pkg = Utils.getPackageName(MoreTypes.asTypeElement(beanDefinition.getType()));

    clazz.getClassCompilationUnit().setPackageDeclaration(pkg);

    if (!iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.GWT2)) {
      clazz.getClassCompilationUnit().addImport(OnFieldAccessed.class);
      clazz.getClassCompilationUnit().addImport(Reflect.class);
      clazz.getClassCompilationUnit().addImport(SyncBeanDef.class);
      clazz.getClassCompilationUnit().addImport(BeanFactory.class);
      clazz.getClassCompilationUnit().addImport(Supplier.class);
      clazz.getClassCompilationUnit().addImport(Provider.class);
      clazz.getClassCompilationUnit().addImport(BeanManager.class);
      clazz.getClassCompilationUnit().addImport(InstanceFactory.class);
    }

    clazz.setClassName(MoreTypes.asTypeElement(beanDefinition.getType()).getSimpleName().toString()
        + BOOTSTRAP_EXTENSION);

    clazz.addField(MoreTypes.asTypeElement(beanDefinition.getType()).getQualifiedName().toString(),
        "instance", Modifier.Keyword.PRIVATE);

    clazz.addFieldWithInitializer(BeanManager.class.getSimpleName(), "beanManager",
        new MethodCallExpr(new NameExpr(BeanManager.class.getCanonicalName() + "Impl"), "get"),
        Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);

  }

  @Override
  public void generateNewInstanceMethodBuilder(ClassBuilder classBuilder) {

  }

  @Override
  protected void generateInitInstanceMethodBuilder(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    MethodDeclaration initMethodDeclaration = classBuilder.addInitInstanceMethod();
    initMethodDeclaration.addParameter(beanDefinition.getType().toString(), "instance");
  }

  @Override
  public void generateInstanceGetMethodBuilder(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    classBuilder.addConstructorDeclaration();

    MethodDeclaration getMethodDeclaration = classBuilder.addMethod("initialize");
    classBuilder.setGetMethodDeclaration(getMethodDeclaration);

    getMethodDeclaration.getBody().get().addAndGetStatement(new MethodCallExpr("runOnStartup"));
    getMethodDeclaration.getBody().get().addAndGetStatement(new MethodCallExpr("doProxyInstance"));
    getMethodDeclaration.getBody().get()
        .addAndGetStatement(new MethodCallExpr("initInstance").addArgument("instance"));

    setDoProxyInstance(classBuilder, beanDefinition);
    setRunOnStartup(classBuilder);
  }

  private void setRunOnStartup(ClassBuilder classBuilder) {
    MethodDeclaration runOnStartup =
        classBuilder.addMethod("runOnStartup", Modifier.Keyword.PRIVATE);
    new StartupGenerator(iocContext).generate(runOnStartup);
  }

  private void setDoProxyInstance(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
    MethodDeclaration doProxyInstance =
        classBuilder.addMethod("doProxyInstance", Modifier.Keyword.PRIVATE);

    if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.J2CL)) {
      ObjectCreationExpr interceptorCreationExpr = new ObjectCreationExpr();
      interceptorCreationExpr.setType(Interceptor.class.getSimpleName());
      interceptorCreationExpr.addArgument(new NameExpr("instance"));

      doProxyInstance.getBody().get()
          .addAndGetStatement(new AssignExpr().setTarget(new VariableDeclarationExpr(
              new ClassOrInterfaceType().setName(Interceptor.class.getSimpleName()), "interceptor"))
              .setValue(interceptorCreationExpr));



      doProxyInstance.getBody().get()
          .addAndGetStatement(new AssignExpr().setTarget(new NameExpr("instance"))
              .setValue(new MethodCallExpr(new NameExpr("interceptor"), "getProxy")));
    }

    if (!iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE)) {
      for (InjectableVariableDefinition fieldPoint : beanDefinition.getFields()) {
        doProxyInstance.getBody().get().addStatement(
            getFieldAccessorExpression(classBuilder, beanDefinition, fieldPoint, "field"));
      }
    }
  }

  @Override
  public void generateDependantFieldDeclaration(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    classBuilder.addConstructorDeclaration();
    Parameter arg = new Parameter();
    arg.setName("application");
    arg.setType(MoreTypes.asTypeElement(beanDefinition.getType()).getSimpleName().toString());

    classBuilder.addParametersToConstructor(arg);

    AssignExpr assign = new AssignExpr().setTarget(new FieldAccessExpr(new ThisExpr(), "instance"))
        .setValue(new NameExpr("application"));
    classBuilder.addStatementToConstructor(assign);
  }

  @Override
  public void generateInstanceGetMethodReturn(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {}

  @Override
  public void write(ClassBuilder clazz, BeanDefinition beanDefinition, GenerationContext context) {
    try {
      String fileName = Utils.getQualifiedName(MoreTypes.asElement(beanDefinition.getType()))
          + BOOTSTRAP_EXTENSION;
      String source = clazz.toSourceCode();
      build(fileName, source, context);
    } catch (IOException e1) {
      // throw new GenerationException(e1);
    }
  }
}
