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
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import io.crysknife.annotation.Generator;
import io.crysknife.client.BeanManager;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.ProducesBeanDefinition;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.lang.model.element.TypeElement;
import java.util.function.Supplier;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/4/19
 */
@Generator(priority = 500)
public class ProducesGenerator extends ScopedBeanGenerator {

  private static final String BEAN_MANAGER_IMPL = "io.crysknife.client.BeanManagerImpl";

  public ProducesGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Produces.class, WiringElementType.METHOD_DECORATOR, this);
  }

  // @Override
  public void generateDependantFieldDeclaration2(ClassBuilder builder, BeanDefinition definition) {
    if (definition instanceof ProducesBeanDefinition) {
      ProducesBeanDefinition producesDefinition = (ProducesBeanDefinition) definition;

      builder.getClassCompilationUnit().addImport(Instance.class);
      builder.getClassCompilationUnit().addImport(Supplier.class);
      builder.getClassCompilationUnit()
          .addImport(MoreElements.asType(producesDefinition.getMethod().getEnclosingElement())
              .getQualifiedName().toString());
      builder.getClassCompilationUnit()
          .addImport(producesDefinition.getMethod().getReturnType().toString());


      builder.addField(BeanManager.class.getSimpleName(), "beanManager", Modifier.Keyword.PRIVATE,
          Modifier.Keyword.FINAL);

      ConstructorDeclaration constructorDeclaration =
          builder.addConstructorDeclaration(Modifier.Keyword.PUBLIC);
      constructorDeclaration.addAndGetParameter(BeanManager.class, "beanManager");

      constructorDeclaration.getBody().addAndGetStatement(
          new AssignExpr().setTarget(new FieldAccessExpr(new ThisExpr(), "beanManager"))
              .setValue(new NameExpr("beanManager")));

      TypeElement instance =
          MoreElements.asType(producesDefinition.getMethod().getEnclosingElement());

      Expression call = getBeanManagerCallExpr(instance);

      ClassOrInterfaceType supplier =
          new ClassOrInterfaceType().setName(Supplier.class.getSimpleName());

      ClassOrInterfaceType type = new ClassOrInterfaceType();
      type.setName(Instance.class.getSimpleName());
      type.setTypeArguments(new ClassOrInterfaceType().setName(instance.toString()));
      supplier.setTypeArguments(type);

      builder.addFieldWithInitializer(supplier, "producer", call, Modifier.Keyword.PRIVATE,
          Modifier.Keyword.FINAL);
    }
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
