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

import io.crysknife.annotation.Generator;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;

import javax.enterprise.event.Observes;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/5/19
 */
@Generator(priority = 1000)
public class ObservesGenerator extends IOCGenerator {

  public ObservesGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Observes.class, WiringElementType.PARAMETER, this);
  }

  @Override
  public void generate(ClassBuilder clazz, io.crysknife.definition.Definition beanDefinition) {

  }

  /*
   * // @Override public void generate(ClassBuilder classBuilder, Definition definition) { if
   * (definition instanceof ExecutableDefinition) { ExecutableDefinition methodDefinition =
   * (ExecutableDefinition) definition;
   * 
   * ExecutableElement method = methodDefinition.getExecutableElement(); if
   * (method.getParameters().size() > 1) { throw new GenerationException(
   * "Method annotated with @Observes must contains only one param " + method.getEnclosingElement()
   * + " " + method); }
   * 
   * classBuilder.getClassCompilationUnit().addImport(Consumer.class);
   * 
   * VariableElement parameter = method.getParameters().get(0);
   * classBuilder.getClassCompilationUnit().addImport("javax.enterprise.event.Event_Factory");
   * MethodCallExpr eventFactory = new MethodCallExpr(new
   * NameExpr("Event_Factory").getNameAsExpression(), "get"); MethodCallExpr getEventHandler = new
   * MethodCallExpr(eventFactory, "get") .addArgument(new FieldAccessExpr(new
   * NameExpr(parameter.asType().toString()), "class"));
   * 
   * Parameter argument = new Parameter(); argument.setName("(event)");
   * 
   * ExpressionStmt expressionStmt = new ExpressionStmt(); VariableDeclarationExpr
   * variableDeclarationExpr = new VariableDeclarationExpr();
   * 
   * VariableDeclarator variableDeclarator = new VariableDeclarator();
   * variableDeclarator.setName(parameter.getEnclosingElement().getSimpleName().toString());
   * 
   * ClassOrInterfaceType consumerClassDecloration = new
   * ClassOrInterfaceType().setName(Consumer.class.getCanonicalName()); consumerClassDecloration
   * .setTypeArguments(new ClassOrInterfaceType().setName(parameter.asType().toString()));
   * variableDeclarator.setType(consumerClassDecloration); variableDeclarator.setInitializer(
   * "event -> this.instance." + method.getSimpleName().toString() + "(event)");
   * variableDeclarationExpr.getVariables().add(variableDeclarator);
   * expressionStmt.setExpression(variableDeclarationExpr);
   * 
   * classBuilder.getGetMethodDeclaration().getBody().get().addAndGetStatement(expressionStmt);
   * 
   * EnclosedExpr castToAbstractEventHandler = new EnclosedExpr(new CastExpr( new
   * ClassOrInterfaceType().setName("io.crysknife.client.internal.AbstractEventHandler"),
   * getEventHandler));
   * 
   * MethodCallExpr addSubscriber = new MethodCallExpr(castToAbstractEventHandler, "addSubscriber")
   * .addArgument(parameter.getEnclosingElement().getSimpleName().toString());
   * 
   * classBuilder.getGetMethodDeclaration().getBody().get().addAndGetStatement(addSubscriber); } }
   */
}
