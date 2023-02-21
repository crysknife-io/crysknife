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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import io.crysknife.client.Reflect;
import io.crysknife.client.internal.proxy.OnFieldAccessed;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.definition.ProducesBeanDefinition;
import io.crysknife.util.Utils;

public class FieldAccessorExpressionCreator {

  protected Expression getFieldAccessorExpression(InjectableVariableDefinition fieldPoint,
      String kind) {

    String varName = "_" + kind + "_" + fieldPoint.getVariableElement().getSimpleName().toString();

    if (fieldPoint.getBeanDefinition() instanceof ProducesBeanDefinition) {
      throw new Error(fieldPoint.getVariableElement().getSimpleName().toString());
    }

    if (kind.equals("constructor")) {
      return new MethodCallExpr(
          new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), varName), "get"), "getInstance");
    }

    FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(new ThisExpr(), "interceptor");
    MethodCallExpr reflect =
        new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
            .addArgument(
                new StringLiteralExpr(Utils.getJsFieldName(fieldPoint.getVariableElement())))
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

}
