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

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.MethodDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.Utils;

import javax.enterprise.event.Observes;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.function.Consumer;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/5/19
 */
@Generator(priority = 1000)
public class ObservesGenerator extends IOCGenerator<MethodDefinition> {

  public ObservesGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Observes.class, WiringElementType.PARAMETER, this);
  }

  @Override
  public void generate(ClassBuilder classBuilder, MethodDefinition methodDefinition) {
    ExecutableElement method = methodDefinition.getExecutableElement();
    BeanDefinition parent = iocContext.getBean(method.getEnclosingElement().asType());
    if (!Utils.isDependent(parent)) {
      return;
    }

    if (method.getParameters().size() > 1) {
      throw new GenerationException("Method annotated with @Observes must contain only one param "
          + method.getEnclosingElement() + " " + method);
    }

    if (method.getModifiers().contains(Modifier.PRIVATE)) {
      throw new GenerationException("Method annotated with @Observes must be non-private "
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


    classBuilder.getClassCompilationUnit().addImport(Consumer.class);

    VariableElement parameter = method.getParameters().get(0);
    TypeMirror parameterTypeMirror =
        iocContext.getGenerationContext().getTypes().erasure(parameter.asType());
    String parameterName = parameter.getEnclosingElement().getSimpleName().toString() + "_"
        + parameterTypeMirror.toString().replaceAll("\\.", "_") + "_"
        + MoreElements.asType(method.getEnclosingElement()).getQualifiedName().toString()
            .replaceAll("\\.", "_");

    classBuilder.getClassCompilationUnit().addImport("javax.enterprise.event.Event_Factory");
    MethodCallExpr eventFactory =
        new MethodCallExpr(new NameExpr("Event_Factory").getNameAsExpression(), "get");
    MethodCallExpr getEventHandler = new MethodCallExpr(eventFactory, "get")
        .addArgument(new FieldAccessExpr(new NameExpr(parameterTypeMirror.toString()), "class"));

    Parameter argument = new Parameter();
    argument.setName("(event)");

    ExpressionStmt expressionStmt = new ExpressionStmt();
    VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();

    VariableDeclarator variableDeclarator = new VariableDeclarator();
    variableDeclarator.setName(parameterName);

    ClassOrInterfaceType consumerClassDeclaration =
        new ClassOrInterfaceType().setName(Consumer.class.getCanonicalName());
    consumerClassDeclaration
        .setTypeArguments(new ClassOrInterfaceType().setName(parameter.asType().toString()));
    variableDeclarator.setType(consumerClassDeclaration);
    variableDeclarator
        .setInitializer("event -> this.instance." + method.getSimpleName().toString() + "(event)");
    variableDeclarationExpr.getVariables().add(variableDeclarator);
    expressionStmt.setExpression(variableDeclarationExpr);

    classBuilder.getInitInstanceMethod().getBody().get().addAndGetStatement(expressionStmt);

    EnclosedExpr castToAbstractEventHandler = new EnclosedExpr(new CastExpr(
        new ClassOrInterfaceType().setName("io.crysknife.client.internal.AbstractEventHandler"),
        getEventHandler));

    MethodCallExpr addSubscriber =
        new MethodCallExpr(castToAbstractEventHandler, "addSubscriber").addArgument(parameterName);

    classBuilder.getInitInstanceMethod().getBody().get().addAndGetStatement(addSubscriber);
  }

}
