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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import io.crysknife.annotation.Generator;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
@Generator(priority = 1)
public class SingletonGenerator extends ScopedBeanGenerator {

  public SingletonGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Singleton.class, WiringElementType.BEAN, this);
    iocContext.register(ApplicationScoped.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generateInstanceGetMethodBuilder(ClassBuilder builder,
      BeanDefinition beanDefinition) {
    super.generateInstanceGetMethodBuilder(builder, beanDefinition);
    BlockStmt body = builder.getGetMethodDeclaration().getBody().get();

    FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
    IfStmt ifStmt = new IfStmt().setCondition(
        new BinaryExpr(instance, new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS));
    ifStmt.setThenStmt(new ReturnStmt(instance));
    body.addAndGetStatement(ifStmt);
    body.addAndGetStatement(generateInstanceInitializer(builder, beanDefinition));
    builder.addField(beanDefinition.getClassName(), "instance", Modifier.Keyword.PRIVATE);
  }
}
