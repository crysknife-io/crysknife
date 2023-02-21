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
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;

import static com.github.javaparser.ast.expr.UnaryExpr.Operator.LOGICAL_COMPLEMENT;

public class InstanceGetMethod implements Step<BeanDefinition> {

  @Override
  public void execute(IOCContext iocContext, ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {

    String clazzName = classBuilder.beanDefinition.getSimpleClassName();
    MethodDeclaration getMethodDeclaration =
        classBuilder.addMethod("getInstance", Modifier.Keyword.PUBLIC);

    getMethodDeclaration.addAnnotation(Override.class);
    getMethodDeclaration.setType(clazzName);

    getMethodDeclaration.getBody().ifPresent(body -> {

      IfStmt ifStmt = new IfStmt().setCondition(new UnaryExpr(
          new MethodCallExpr(new MethodCallExpr(new NameExpr("beanDef"), "getScope"), "equals")
              .addArgument(new FieldAccessExpr(new NameExpr("Dependent"), "class")),
          LOGICAL_COMPLEMENT));

      body.addAndGetStatement(ifStmt);
      BlockStmt blockStmt = new BlockStmt();

      blockStmt.addAndGetStatement(new IfStmt()
          .setCondition(new BinaryExpr(new NameExpr("instance"), new NullLiteralExpr(),
              BinaryExpr.Operator.NOT_EQUALS))
          .setThenStmt(
              new ReturnStmt(new CastExpr().setType(new ClassOrInterfaceType().setName(clazzName))
                  .setExpression(new NameExpr("instance")))));
      ifStmt.setThenStmt(blockStmt);

      body.addAndGetStatement(
          new AssignExpr()
              .setTarget(new VariableDeclarationExpr(new ClassOrInterfaceType()
                  .setName(classBuilder.beanDefinition.getSimpleClassName()), "instance"))
              .setValue(new MethodCallExpr("createInstanceInternal")));

      body.addAndGetStatement(new MethodCallExpr("initInstance").addArgument("instance"));
      body.addAndGetStatement(new ReturnStmt(new NameExpr("instance")));
    });
  }
}
