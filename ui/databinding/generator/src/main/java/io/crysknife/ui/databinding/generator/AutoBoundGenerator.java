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
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.databinding.client.api.AutoBound;
import io.crysknife.ui.databinding.client.api.Bound;
import io.crysknife.ui.databinding.client.api.Convert;
import io.crysknife.ui.databinding.client.api.DataBinder;
import io.crysknife.ui.databinding.client.api.handler.list.BindableListChangeHandler;
import io.crysknife.util.Utils;
import jsinterop.base.Js;
import org.gwtproject.user.client.TakesValue;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/1/21
 */
@Generator(priority = 100003)
public class AutoBoundGenerator extends IOCGenerator<InjectableVariableDefinition> {

  public AutoBoundGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  /**
   * @param bindableType The root type the given property chain is resolved against. Not null.
   * @param propertyChain The data binding property chain to validate. Not null.
   * @return The type of the given property within the given bindable type.
   */
  public static TypeMirror getPropertyType(final TypeMirror bindableType,
      final String propertyChain) {
    if ("this".equals(propertyChain)) {
      return bindableType;
    }
    final int dotPos = propertyChain.indexOf('.');
    if (dotPos >= 0) {
      final String firstProp = propertyChain.substring(0, dotPos);
      final String subChain = propertyChain.substring(dotPos + 1);
      return getPropertyType(getMethod(bindableType, firstProp).asType(), subChain);
    } else {
      return getMethod(bindableType, propertyChain).asType();
    }
  }

  private static VariableElement getMethod(TypeMirror element, String name) {
    return ElementFilter.fieldsIn(MoreTypes.asElement(element).getEnclosedElements()).stream()
        .filter(variableElement -> variableElement.getSimpleName().toString().equals(name))
        .findFirst().orElseThrow((Supplier<Error>) () -> {
          throw new RuntimeException("Cannot process bindable " + element);
        });
  }

  @Override
  public void register() {
    iocContext.register(AutoBound.class, WiringElementType.FIELD_DECORATOR, this); // PARAMETER
  }

  public void generate(ClassBuilder classBuilder, InjectableVariableDefinition field) {
    Set<VariableElement> autoBound = ElementFilter
        .fieldsIn(MoreTypes.asElement(field.getVariableElement().getEnclosingElement().asType())
            .getEnclosedElements())
        .stream().filter(elm -> elm.getAnnotation(AutoBound.class) != null)
        .collect(Collectors.toSet());

    Set<VariableElement> bounds = ElementFilter
        .fieldsIn(MoreTypes.asElement(field.getVariableElement().getEnclosingElement().asType())
            .getEnclosedElements())
        .stream().filter(elm -> elm.getAnnotation(Bound.class) != null).collect(Collectors.toSet());

    if (autoBound.size() > 1) {
      throw new GenerationException(
          "only one elemental annotated with @AutoBound must be presented at "
              + field.getBeanDefinition().getQualifiedName());
    }

    // bounds
    classBuilder.getInitInstanceMethod().getBody().ifPresent(
        body -> autoBound.stream().forEach(dataBinderVariableElement -> bounds.forEach(bound -> {
          Expression fieldAccessExpr = generationUtils
              .getFieldAccessCallExpr(field.getBeanDefinition(), dataBinderVariableElement);
          MethodCallExpr call =
              new MethodCallExpr(new NameExpr(Js.class.getCanonicalName()), "uncheckedCast")
                  .addArgument(fieldAccessExpr);
          call.setTypeArguments(
              new ClassOrInterfaceType().setName(DataBinder.class.getCanonicalName()));
          MethodCallExpr bind = new MethodCallExpr(call, "bind");
          String boundName = bound.getAnnotation(Bound.class).property().equals("")
              ? bound.getSimpleName().toString()
              : bound.getAnnotation(Bound.class).property();

          boolean onKeyUp = bound.getAnnotation(Bound.class).onKeyUp();

          TypeMirror hasValues = iocContext.getTypeMirror(TakesValue.class);
          TypeMirror check = iocContext.getGenerationContext().getTypes().erasure(bound.asType());
          TypeMirror genericType =
              MoreTypes.asDeclared(dataBinderVariableElement.asType()).getTypeArguments().get(0);

          if (iocContext.getGenerationContext().getTypes().isAssignable(check, hasValues)) {
            long count = Utils
                .getAllMethodsIn(iocContext.getGenerationContext().getElements(),
                    MoreTypes.asTypeElement(bound.asType()))
                .stream().filter(method -> method.getSimpleName().toString().equals("getValue"))
                .map(e -> iocContext.getGenerationContext().getTypes()
                    .asMemberOf(MoreTypes.asDeclared(bound.asType()), e))
                .map(e -> (ExecutableType) e).count();
            if (count == 1) {
              boundName = "this";
            }
          }

          bind.addArgument(
              generationUtils.getFieldAccessCallExpr(field.getBeanDefinition(), bound));
          bind.addArgument(new StringLiteralExpr(boundName));
          bind.addArgument(coverterStatement(bound.getAnnotation(Bound.class), bound.asType(),
              getPropertyType(genericType, boundName)));
          bind.addArgument(new NullLiteralExpr());
          bind.addArgument(new NameExpr(onKeyUp + ""));
          body.addAndGetStatement(bind);
        })));
  }

  private Expression coverterStatement(final Bound bound, final TypeMirror boundType,
      final TypeMirror propertyType) {
    TypeMirror converter = null;
    try {
      Class temp = bound.converter();
    } catch (javax.lang.model.type.MirroredTypeException e) {
      converter = e.getTypeMirror();
    }

    TypeMirror NO_CONVERTER = iocContext.getGenerationContext().getElements()
        .getTypeElement(Bound.NO_CONVERTER.class.getCanonicalName()).asType();
    if (iocContext.getGenerationContext().getTypes().isSameType(converter, NO_CONVERTER)) {
      final Optional<TypeMirror> valueType;
      TypeMirror boundTypeErased = iocContext.getGenerationContext().getTypes().erasure(boundType);
      TypeMirror takesValue = iocContext.getGenerationContext().getElements()
          .getTypeElement(TakesValue.class.getCanonicalName()).asType();

      TypeMirror bindableListChangeHandler = iocContext.getGenerationContext().getElements()
          .getTypeElement(BindableListChangeHandler.class.getCanonicalName()).asType();

      iocContext.getGenerationContext().getTypes().isAssignable(boundTypeErased, takesValue);

      if (iocContext.getGenerationContext().getTypes().isAssignable(boundTypeErased,
          iocContext.getGenerationContext().getTypes().erasure(takesValue))) {
        valueType = Utils
            .getAllMethodsIn(iocContext.getGenerationContext().getElements(),
                MoreTypes.asTypeElement(boundType))
            .stream().filter(method -> method.getSimpleName().toString().equals("getValue"))
            .filter(method -> method.getParameters().isEmpty())
            .map(e -> iocContext.getGenerationContext().getTypes()
                .asMemberOf(MoreTypes.asDeclared(boundType), e))
            .map(method -> ((ExecutableType) method)).map(method -> method.getReturnType())
            .findFirst();
      } else if (iocContext.getGenerationContext().getTypes().isAssignable(boundTypeErased,
          bindableListChangeHandler)) {
        valueType = Optional.ofNullable(bindableListChangeHandler);
      } else {
        valueType = Optional.empty();
      }

      return valueType.map(type -> (Expression) new MethodCallExpr(
          new NameExpr(Convert.class.getCanonicalName()), "getConverter")
              .addArgument(new NameExpr(
                  iocContext.getGenerationContext().getTypes().erasure(propertyType).toString()
                      + ".class"))
              .addArgument(
                  new NameExpr(iocContext.getGenerationContext().getTypes().erasure(type).toString()
                      + ".class")))
          .orElse(new NullLiteralExpr());
    } else {
      return new ObjectCreationExpr().setType(bound.converter());
    }
  }
}
