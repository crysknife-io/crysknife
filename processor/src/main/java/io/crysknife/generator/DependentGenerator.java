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

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import io.crysknife.annotation.Generator;
import io.crysknife.client.internal.proxy.Interceptor;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.definition.InjectionParameterDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

import jakarta.enterprise.context.Dependent;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
@Generator(priority = 1)
public class DependentGenerator extends ScopedBeanGenerator {

  public DependentGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Dependent.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generateInstanceGetMethodBuilder(ClassBuilder builder,
      BeanDefinition beanDefinition) {
    super.generateInstanceGetMethodBuilder(builder, beanDefinition);

    builder.getGetMethodDeclaration().getBody().ifPresent(body -> {
      NameExpr instance = new NameExpr("instance");
      Expression instanceFieldAssignExpr =
          generateInstanceInitializerNewObjectExpr(builder, beanDefinition);
      body.addAndGetStatement(
          new AssignExpr(instance, instanceFieldAssignExpr, AssignExpr.Operator.ASSIGN));
    });
  }

  protected Expression generateInstanceInitializerNewObjectExpr(ClassBuilder classBuilder,
      BeanDefinition definition) {
    ObjectCreationExpr newInstance = generateNewInstanceCreationExpr(definition);
    Set<InjectionParameterDefinition> params = definition.getConstructorParams();
    Iterator<InjectionParameterDefinition> injectionPointDefinitionIterator = params.iterator();
    while (injectionPointDefinitionIterator.hasNext()) {
      InjectableVariableDefinition argument = injectionPointDefinitionIterator.next();
      newInstance.addArgument(
          getFieldAccessorExpression(classBuilder, definition, argument, "constructor"));
    }

    Expression instanceFieldAssignExpr;

    if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.J2CL)) {
      FieldAccessExpr interceptor = new FieldAccessExpr(new ThisExpr(), "interceptor");

      ObjectCreationExpr interceptorCreationExpr = new ObjectCreationExpr();
      interceptorCreationExpr.setType(Interceptor.class.getSimpleName());
      interceptorCreationExpr.addArgument(newInstance);

      classBuilder.getGetMethodDeclaration().getBody().get().addAndGetStatement(
          new AssignExpr().setTarget(interceptor).setValue(interceptorCreationExpr));

      instanceFieldAssignExpr = new MethodCallExpr(interceptor, "getProxy");
    } else {
      instanceFieldAssignExpr = newInstance;
    }
    return instanceFieldAssignExpr;
  }

}
