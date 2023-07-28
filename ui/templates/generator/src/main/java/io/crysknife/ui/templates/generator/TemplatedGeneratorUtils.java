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
import elemental2.dom.HTMLElement;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

public class TemplatedGeneratorUtils {

  private ProcessingEnvironment processingEnvironment;

  public TemplatedGeneratorUtils(IOCContext iocContext) {
    this.processingEnvironment = iocContext.getGenerationContext().getProcessingEnvironment();
  }

  public String getGetRootElementMethodName(DataElementInfo.Kind kind) {
    if (kind.equals(DataElementInfo.Kind.IsElement)) {
      return "getElement";
    }
    throw new GenerationException("Unable to find type of " + kind);
  }


  public Expression getInstanceMethodName(DataElementInfo.Kind kind) {
    return new MethodCallExpr(new NameExpr("instance"), getGetRootElementMethodName(kind));
  }


  public Expression getInstanceCallExpression(TemplateContext templateContext) {
    DataElementInfo.Kind kind = getDataElementInfoKind(templateContext.getDataElementType());
    return getInstanceMethodName(kind);
  }

  public DataElementInfo.Kind getDataElementInfoKind(TypeMirror dataElementType) {
    if (isAssignable(dataElementType, HTMLElement.class)) {
      return DataElementInfo.Kind.HTMLElement;
    } else if (isAssignable(dataElementType, io.crysknife.client.IsElement.class)) {
      return DataElementInfo.Kind.IsElement;
    } else {
      return DataElementInfo.Kind.Custom;
    }
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
