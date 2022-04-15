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

package io.crysknife.ui.templates.generator;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.google.auto.common.MoreTypes;
import elemental2.dom.HTMLElement;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import jsinterop.base.Js;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.Optional;

public class TemplatedGeneratorUtils {

  private IOCContext iocContext;

  private ProcessingEnvironment processingEnvironment;

  public TypeElement isWidgetType;

  public TypeElement widgetType;

  public TemplatedGeneratorUtils(IOCContext iocContext) {
    this.iocContext = iocContext;
    this.processingEnvironment = iocContext.getGenerationContext().getProcessingEnvironment();

    isWidgetType = iocContext.getGenerationContext().getElements()
        .getTypeElement("org.gwtproject.user.client.ui.IsWidget");
    widgetType = iocContext.getGenerationContext().getElements()
        .getTypeElement("org.gwtproject.user.client.ui.Widget");
  }

  public boolean maybeGwtWidget(TypeMirror dataElementType) {
    if (isWidgetType == null) {
      return false;
    }
    return isAssignable(dataElementType, isWidgetType.asType());
  }

  public boolean maybeGwtDom(TypeMirror dataElementType) {
    TypeElement element = iocContext.getGenerationContext().getElements()
        .getTypeElement("org.gwtproject.dom.client.Element");
    return isAssignable(dataElementType, element.asType());
  }

  public String getGetRootElementMethodName(TemplateContext templateContext) {
    DataElementInfo.Kind kind = getDataElementInfoKind(templateContext.getDataElementType());
    return getGetRootElementMethodName(kind);
  }

  public String getGetRootElementMethodName(DataElementInfo.Kind kind) {
    if (kind.equals(DataElementInfo.Kind.IsElement)) {
      return "getElement";
    } else if (kind.equals(DataElementInfo.Kind.IsWidget)) {
      return "getElement";
    }
    throw new GenerationException("Unable to find type of " + kind);
  }

  public String getGetRootElementMethodName(DataElementInfo element) {
    return getGetRootElementMethodName(element.getKind());
  }

  public Expression getInstanceMethodName(DataElementInfo.Kind kind) {
    MethodCallExpr expr =
        new MethodCallExpr(new NameExpr("instance"), getGetRootElementMethodName(kind));
    if (kind.equals(DataElementInfo.Kind.IsWidget)) {
      uncheckedCastCall(expr, isWidgetType.toString());
    }
    return expr;
  }

  public Expression uncheckedCastCall(Expression target, String clazz) {
    return new MethodCallExpr(new NameExpr(Js.class.getCanonicalName()),
        "<" + clazz + ">uncheckedCast").addArgument(target);
  }

  public Expression getInstanceCallExpression(TemplateContext templateContext) {
    DataElementInfo.Kind kind = getDataElementInfoKind(templateContext.getDataElementType());
    Expression instance = getInstanceMethodName(kind);

    if (kind.equals(DataElementInfo.Kind.IsWidget)) {
      return uncheckedCastCall(instance, HTMLElement.class.getCanonicalName());
    }
    return instance;
  }

  public DataElementInfo.Kind getDataElementInfoKind(TypeMirror dataElementType) {
    if (isAssignable(dataElementType, HTMLElement.class)) {
      return DataElementInfo.Kind.HTMLElement;
    } else if (isAssignable(dataElementType, io.crysknife.client.IsElement.class)) {
      return DataElementInfo.Kind.IsElement;
    } else if (maybeGwtWidget(dataElementType)) {
      return DataElementInfo.Kind.IsWidget;
    } else if (maybeGwtDom(dataElementType)) {
      return DataElementInfo.Kind.GWT_DOM;
    } else {
      return DataElementInfo.Kind.Custom;
    }
  }

  public boolean implementsIsElement(TemplateContext templateContext) {
    return ElementFilter
        .methodsIn(MoreTypes.asElement(templateContext.getDataElementType()).getEnclosedElements())
        .stream().filter(elm -> elm.getSimpleName().toString().equals("getElement"))
        .filter(elm -> elm.getParameters().isEmpty())
        .filter(elm -> elm.getModifiers().contains(Modifier.PUBLIC)).findFirst().isPresent();
  }

  public boolean isAssignable(TypeElement subType, Class<?> baseType) {
    return isAssignable(subType.asType(), baseType);
  }

  public boolean isAssignable(TypeMirror subType, Class<?> baseType) {
    return isAssignable(subType, getTypeMirror(baseType));
  }

  public boolean isAssignable(TypeMirror subType, TypeMirror baseType) {
    return processingEnvironment.getTypeUtils().isAssignable(
        processingEnvironment.getTypeUtils().erasure(subType),
        processingEnvironment.getTypeUtils().erasure(baseType));
  }

  private TypeMirror getTypeMirror(Class<?> c) {
    return processingEnvironment.getElementUtils().getTypeElement(c.getName()).asType();
  }
}
