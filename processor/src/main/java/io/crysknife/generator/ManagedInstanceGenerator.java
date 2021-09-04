/*
 * Copyright © 2021 Treblereel
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
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.google.auto.common.MoreTypes;
import elemental2.dom.DomGlobal;
import io.crysknife.annotation.Generator;
import io.crysknife.client.ManagedInstance;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.client.internal.ManagedInstanceImpl;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.Definition;
import io.crysknife.generator.point.FieldPoint;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/27/21
 */
@Generator
public class ManagedInstanceGenerator extends BeanIOCGenerator {

  public ManagedInstanceGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint) {

    clazz.getClassCompilationUnit().addImport(ManagedInstance.class);
    clazz.getClassCompilationUnit().addImport(ManagedInstanceImpl.class);
    clazz.getClassCompilationUnit().addImport(InstanceImpl.class);

    TypeMirror param =
        MoreTypes.asDeclared(fieldPoint.getField().asType()).getTypeArguments().get(0);

    ObjectCreationExpr instance = new ObjectCreationExpr().setType(ManagedInstanceImpl.class)
        .addArgument(new NameExpr(param.toString() + ".class"))
        .addArgument(new NameExpr("beanManager"));

    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(instance));

    return new ObjectCreationExpr().setType(InstanceImpl.class).addArgument(lambda);

  }

  @Override
  public void register() {
    iocContext.register(Inject.class, ManagedInstance.class, WiringElementType.FIELD_TYPE, this);
    iocContext.register(Inject.class, Instance.class, WiringElementType.FIELD_TYPE, this);
    iocContext.getBlacklist().add(ManagedInstance.class.getCanonicalName());
    iocContext.getBlacklist().add(Instance.class.getCanonicalName());
  }

  @Override
  public void generate(ClassBuilder clazz, Definition beanDefinition) {
    // do nothing
  }
}
