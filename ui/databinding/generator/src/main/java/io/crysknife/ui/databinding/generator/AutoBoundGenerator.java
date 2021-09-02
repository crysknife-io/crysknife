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

package io.crysknife.ui.databinding.generator;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import io.crysknife.annotation.Generator;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;
import io.crysknife.generator.definition.Definition;
import io.crysknife.ui.databinding.client.api.AutoBound;
import io.crysknife.ui.databinding.client.api.Bound;
import io.crysknife.ui.databinding.client.api.DataBinder;
import jsinterop.base.Js;

import javax.lang.model.element.VariableElement;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/1/21
 */
@Generator(priority = 100003)
public class AutoBoundGenerator extends IOCGenerator {

  public AutoBoundGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(AutoBound.class, WiringElementType.FIELD_DECORATOR, this); // PARAMETER
  }

  @Override
  public void generateBeanFactory(ClassBuilder classBuilder, Definition definition) {
    if (definition instanceof BeanDefinition) {
      BeanDefinition bean = (BeanDefinition) definition;

      Set<VariableElement> autoBound = bean.getType().getEnclosedElements().stream()
          .filter(elm -> elm.getKind().isField()).map(elm -> MoreElements.asVariable(elm))
          .filter(elm -> elm.getAnnotation(AutoBound.class) != null).collect(Collectors.toSet());

      Set<VariableElement> bounds = bean.getType().getEnclosedElements().stream()
          .filter(elm -> elm.getKind().isField()).map(elm -> MoreElements.asVariable(elm))
          .filter(elm -> elm.getAnnotation(Bound.class) != null).collect(Collectors.toSet());

      if (autoBound.size() > 1) {
        throw new GenerationException(
            "only one elemental annotated with !AutoBound must be presented at " + bean.getType());
      }

      // bounds
      classBuilder.getGetMethodDeclaration().getBody().ifPresent(
          body -> autoBound.stream().forEach(dataBinderVariableElement -> bounds.forEach(bound -> {
            Expression fieldAccessExpr =
                generationUtils.getFieldAccessCallExpr(bean, dataBinderVariableElement);
            MethodCallExpr call =
                new MethodCallExpr(new NameExpr(Js.class.getCanonicalName()), "uncheckedCast")
                    .addArgument(fieldAccessExpr);
            call.setTypeArguments(
                new ClassOrInterfaceType().setName(DataBinder.class.getCanonicalName()));
            MethodCallExpr bind = new MethodCallExpr(call, "bind");
            String boundName = bound.getAnnotation(Bound.class).property().equals("")
                ? bound.getSimpleName().toString()
                : bound.getAnnotation(Bound.class).property();

            bind.addArgument(generationUtils.getFieldAccessCallExpr(bean, bound));
            bind.addArgument(new StringLiteralExpr(boundName));
            body.addAndGetStatement(bind);
          })));
    }
  }
}
