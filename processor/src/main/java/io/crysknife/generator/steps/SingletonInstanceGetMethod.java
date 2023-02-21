/*
 * Copyright © 2023 Treblereel
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

package io.crysknife.generator.steps;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.client.internal.proxy.Interceptor;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.definition.InjectionParameterDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;

import java.util.Iterator;
import java.util.Set;

public class SingletonInstanceGetMethod implements Step<BeanDefinition> {

  private final InstanceGetMethod instanceGetMethod = new InstanceGetMethod();

  private final FieldAccessorExpressionCreator fieldAccessorExpressionCreator =
      new FieldAccessorExpressionCreator();

  @Override
  public void execute(IOCContext iocContext, ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    instanceGetMethod.execute(iocContext, classBuilder, beanDefinition);
    String clazzName = beanDefinition.getSimpleClassName();
    BlockStmt body = classBuilder.getGetMethodDeclaration().getBody().get();

    FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
    IfStmt ifStmt = new IfStmt().setCondition(
        new BinaryExpr(instance, new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS));
    ifStmt.setThenStmt(
        new ReturnStmt(new CastExpr().setType(new ClassOrInterfaceType().setName(clazzName))
            .setExpression(new NameExpr("instance"))));
    body.addAndGetStatement(ifStmt);
    body.addAndGetStatement(generateInstanceInitializer(iocContext, classBuilder, beanDefinition));
  }

  protected Expression generateInstanceInitializer(IOCContext iocContext, ClassBuilder classBuilder,
      BeanDefinition definition) {
    NameExpr instance = new NameExpr("instance");
    Expression instanceFieldAssignExpr =
        generateInstanceInitializerNewObjectExpr(iocContext, classBuilder, definition);
    return new AssignExpr().setTarget(instance).setValue(instanceFieldAssignExpr);
  }

  protected Expression generateInstanceInitializerNewObjectExpr(IOCContext iocContext,
      ClassBuilder classBuilder, BeanDefinition definition) {
    ObjectCreationExpr newInstance = generateNewInstanceCreationExpr(definition);
    Set<InjectionParameterDefinition> params = definition.getConstructorParams();
    Iterator<InjectionParameterDefinition> injectionPointDefinitionIterator = params.iterator();
    while (injectionPointDefinitionIterator.hasNext()) {
      InjectableVariableDefinition argument = injectionPointDefinitionIterator.next();
      Expression fieldAccessExpr =
          fieldAccessorExpressionCreator.getFieldAccessorExpression(argument, "constructor");
      newInstance.addArgument(fieldAccessExpr);
    }
    if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.J2CL)) {
      FieldAccessExpr interceptor = new FieldAccessExpr(new ThisExpr(), "interceptor");
      ObjectCreationExpr interceptorCreationExpr = new ObjectCreationExpr();
      interceptorCreationExpr.setType(Interceptor.class.getSimpleName());
      interceptorCreationExpr.addArgument(newInstance);

      classBuilder.getGetMethodDeclaration().getBody().get().addAndGetStatement(
          new AssignExpr().setTarget(interceptor).setValue(interceptorCreationExpr));

      return new MethodCallExpr(interceptor, "getProxy");
    }
    return newInstance;
  }

  protected static ObjectCreationExpr generateNewInstanceCreationExpr(BeanDefinition definition) {
    ObjectCreationExpr newInstance = new ObjectCreationExpr();
    return newInstance.setType(definition.getSimpleClassName());
  }

}
