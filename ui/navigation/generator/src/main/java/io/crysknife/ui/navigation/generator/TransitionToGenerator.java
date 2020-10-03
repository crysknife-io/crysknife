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

package io.crysknife.ui.navigation.generator;

import javax.inject.Inject;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.generator.ScopedBeanGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;
import io.crysknife.generator.definition.Definition;
import io.crysknife.generator.point.FieldPoint;
import io.crysknife.ui.navigation.client.local.Navigation;
import io.crysknife.ui.navigation.client.local.TransitionTo;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/20
 */
@Generator
public class TransitionToGenerator extends ScopedBeanGenerator {

  public static final String TRANSITION_TO_FACTORY = "TransitionTo_Factory";

  public TransitionToGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, TransitionTo.class, WiringElementType.BEAN, this);
    iocContext.getBlacklist().add(TransitionTo.class.getCanonicalName());
  }

  @Override
  public void generateBeanFactory(ClassBuilder clazz, Definition definition) {

  }

  @Override
  public Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint,
      BeanDefinition beanDefinition) {
    clazz.getClassCompilationUnit().addImport(TransitionTo.class);

    return new ObjectCreationExpr().setType(TransitionTo.class)
        .addArgument(MoreTypes.asDeclared(fieldPoint.getField().asType()).getTypeArguments().get(0)
            .toString() + ".class")
        .addArgument(
            new MethodCallExpr(
                new MethodCallExpr(new MethodCallExpr(new NameExpr("BeanManagerImpl"), "get"),
                    "lookupBean").addArgument(Navigation.class.getCanonicalName() + ".class"),
                "get"));
  }
}
