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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.annotation.Generator;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

import javax.enterprise.inject.Produces;
import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/4/19
 */
@Generator(priority = 500)
public class ProducesGenerator extends ScopedBeanGenerator {

  private static final String BEAN_MANAGER_IMPL = "io.crysknife.client.BeanManagerImpl";

  public ProducesGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Produces.class, WiringElementType.METHOD_DECORATOR, this);
  }

  private Expression getBeanManagerCallExpr(TypeElement instance) {
    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(new MethodCallExpr(
        new MethodCallExpr(
            new ClassOrInterfaceType().setName(BEAN_MANAGER_IMPL).getNameAsExpression(), "get"),
        "lookupBean").addArgument(instance.getQualifiedName().toString() + ".class")));

    return lambda;
  }

}
