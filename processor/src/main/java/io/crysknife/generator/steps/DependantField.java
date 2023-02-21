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

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import io.crysknife.annotation.Generator;
import io.crysknife.client.BeanManager;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;

import java.util.Comparator;

public class DependantField implements Step<BeanDefinition> {

  private final FieldAccessorExpressionCreator fieldAccessorExpressionCreator =
      new FieldAccessorExpressionCreator();

  @Override
  public void execute(IOCContext iocContext, ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    ConstructorDeclaration constructorDeclaration =
        classBuilder.addConstructorDeclaration(Modifier.Keyword.PUBLIC);
    constructorDeclaration.addAndGetParameter(BeanManager.class, "beanManager");

    constructorDeclaration.getBody()
        .addAndGetStatement(new MethodCallExpr("super").addArgument("beanManager"));

    if (!iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE)) {
      beanDefinition.getFields().forEach(fieldPoint -> {
        Expression expr =
            fieldAccessorExpressionCreator.getFieldAccessorExpression(fieldPoint, "field");
        classBuilder.getGetMethodDeclaration().getBody().get().addStatement(expr);
      });
    }

    beanDefinition.getDecorators().stream()
        .sorted(
            Comparator.comparingInt(o -> o.getClass().getAnnotation(Generator.class).priority()))
        .forEach(gen -> gen.generate(classBuilder, beanDefinition));
  }

}
