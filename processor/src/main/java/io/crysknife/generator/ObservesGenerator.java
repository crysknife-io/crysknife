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

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.client.internal.AbstractEventHandler;
import io.crysknife.client.internal.event.EventManager;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.MethodDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

import jakarta.enterprise.event.Observes;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.function.BiConsumer;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/5/19
 */
@Generator(priority = 1000)
public class ObservesGenerator extends IOCGenerator<MethodDefinition> {

  public ObservesGenerator(TreeLogger logger, IOCContext iocContext) {
    super(logger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Observes.class, WiringElementType.PARAMETER, this);
  }

  @Override
  public void generate(ClassBuilder classBuilder, MethodDefinition methodDefinition) {
    ExecutableElement method = methodDefinition.getExecutableElement();
    BeanDefinition parent = iocContext.getBean(method.getEnclosingElement().asType());

    if (method.getParameters().size() > 1) {
      throw new GenerationException("Method annotated with @Observes must contain only one param "
          + method.getEnclosingElement() + " " + method);
    }

    if (method.getModifiers().contains(Modifier.STATIC)) {
      throw new GenerationException("Method annotated with @Observes must be non-static "
          + method.getEnclosingElement() + " " + method);
    }

    if (!MoreElements.getPackage(MoreTypes.asTypeElement(classBuilder.beanDefinition.getType()))
        .equals(MoreElements.getPackage(method.getEnclosingElement()))
        && !method.getModifiers().contains(Modifier.PUBLIC)) {
      throw new GenerationException(
          String.format("Method %s annotated with @Observes is not accessible from parent bean %s",
              (method.getEnclosingElement().toString() + "." + method), parent.getType()));
    }

    classBuilder.getClassCompilationUnit().addImport(AbstractEventHandler.class);
    classBuilder.getClassCompilationUnit().addImport(BiConsumer.class);
    classBuilder.getClassCompilationUnit().addImport(EventManager.class);

    VariableElement parameter = method.getParameters().get(0);
    TypeMirror parameterTypeMirror =
        iocContext.getGenerationContext().getTypes().erasure(parameter.asType());

    String consumer = parameter.getEnclosingElement().getSimpleName().toString() + "_"
        + parameterTypeMirror.toString().replaceAll("\\.", "_") + "_"
        + MoreElements.asType(method.getEnclosingElement()).getQualifiedName().toString()
            .replaceAll("\\.", "_");


    addConsumerField(classBuilder, consumer, methodDefinition, parameter);
    addSubscriberCall(classBuilder, parameter, consumer);
    addUnSubscriberCall(classBuilder, parameter, consumer);
  }

  private void addUnSubscriberCall(ClassBuilder classBuilder, VariableElement parameter,
      String consumer) {
    TypeMirror parameterTypeMirror =
        iocContext.getGenerationContext().getTypes().erasure(parameter.asType());

    classBuilder.getOnDestroyMethod().getBody().ifPresent(body -> {
      body.addStatement(new MethodCallExpr(
          new EnclosedExpr(new CastExpr(new ClassOrInterfaceType().setName("AbstractEventHandler"),
              new MethodCallExpr(
                  new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                      .addArgument("EventManager.class"), "getInstance"),
                  "get").addArgument(parameterTypeMirror.toString() + ".class"))),
          "removeSubscriber").addArgument("instance").addArgument(consumer));
    });
  }

  private void addSubscriberCall(ClassBuilder classBuilder, VariableElement parameter,
      String consumer) {
    TypeMirror parameterTypeMirror =
        iocContext.getGenerationContext().getTypes().erasure(parameter.asType());

    classBuilder.getInitInstanceMethod().getBody().ifPresent(body -> {
      body.addStatement(new MethodCallExpr(
          new EnclosedExpr(new CastExpr(new ClassOrInterfaceType().setName("AbstractEventHandler"),
              new MethodCallExpr(
                  new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                      .addArgument("EventManager.class"), "getInstance"),
                  "get").addArgument(parameterTypeMirror.toString() + ".class"))),
          "addSubscriber").addArgument("instance").addArgument(consumer));
    });
  }

  private void addConsumerField(ClassBuilder classBuilder, String parameterName,
      MethodDefinition methodDefinition, VariableElement parameter) {
    ExecutableElement method = methodDefinition.getExecutableElement();
    BeanDefinition parent = iocContext.getBean(method.getEnclosingElement().asType());

    TypeMirror caller = iocContext.getTypeMirror(Observes.class);
    Statement call = generationUtils.generateMethodCall(caller, method, new NameExpr("event"));

    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.getParameters().add(new Parameter().setName("event").setType(new UnknownType()));
    lambda.getParameters().add(new Parameter().setName("instance").setType(new UnknownType()));
    lambda.setBody(call);

    ClassOrInterfaceType consumerClassDeclaration =
        new ClassOrInterfaceType().setName(BiConsumer.class.getCanonicalName());
    consumerClassDeclaration.setTypeArguments(
        new ClassOrInterfaceType().setName(parameter.asType().toString()),
        new ClassOrInterfaceType().setName(
            iocContext.getGenerationContext().getTypes().erasure(parent.getType()).toString()));

    classBuilder.addFieldWithInitializer(consumerClassDeclaration, parameterName, lambda,
        Keyword.PRIVATE, Keyword.FINAL);
  }

}
