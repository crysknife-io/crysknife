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

package io.crysknife.ui.elemental2;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLAreaElement;
import elemental2.dom.HTMLAudioElement;
import elemental2.dom.HTMLBRElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLDataListElement;
import elemental2.dom.HTMLDetailsElement;
import elemental2.dom.HTMLDialogElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLEmbedElement;
import elemental2.dom.HTMLFieldSetElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLHRElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLLegendElement;
import elemental2.dom.HTMLMapElement;
import elemental2.dom.HTMLMenuElement;
import elemental2.dom.HTMLMenuItemElement;
import elemental2.dom.HTMLMeterElement;
import elemental2.dom.HTMLOListElement;
import elemental2.dom.HTMLObjectElement;
import elemental2.dom.HTMLOptGroupElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLOutputElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLParamElement;
import elemental2.dom.HTMLPreElement;
import elemental2.dom.HTMLProgressElement;
import elemental2.dom.HTMLQuoteElement;
import elemental2.dom.HTMLScriptElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLSourceElement;
import elemental2.dom.HTMLTableCaptionElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableColElement;
import elemental2.dom.HTMLTableElement;
import elemental2.dom.HTMLTableRowElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.HTMLTrackElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.HTMLVideoElement;
import io.crysknife.annotation.Generator;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.BeanIOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.Definition;
import io.crysknife.generator.point.FieldPoint;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/7/19
 */
@Generator(priority = 100000)
public class Elemenatal2FactoryGenerator extends BeanIOCGenerator {

  private static final SetMultimap<Class, String> HTML_ELEMENTS = HashMultimap.create();

  static {
    HTML_ELEMENTS.put(HTMLAnchorElement.class, "a");
    HTML_ELEMENTS.put(HTMLAreaElement.class, "area");
    HTML_ELEMENTS.put(HTMLAudioElement.class, "audio");
    HTML_ELEMENTS.put(HTMLQuoteElement.class, "blockquote");
    HTML_ELEMENTS.put(HTMLBRElement.class, "br");
    HTML_ELEMENTS.put(HTMLButtonElement.class, "button");
    HTML_ELEMENTS.put(HTMLCanvasElement.class, "canvas");
    HTML_ELEMENTS.put(HTMLTableCaptionElement.class, "caption");
    HTML_ELEMENTS.put(HTMLTableColElement.class, "col");
    HTML_ELEMENTS.put(HTMLDataListElement.class, "datalist");
    HTML_ELEMENTS.put(HTMLDetailsElement.class, "details");
    HTML_ELEMENTS.put(HTMLDialogElement.class, "dialog");
    HTML_ELEMENTS.put(HTMLDivElement.class, "div");
    HTML_ELEMENTS.put(HTMLEmbedElement.class, "embed");
    HTML_ELEMENTS.put(HTMLFieldSetElement.class, "fieldset");
    HTML_ELEMENTS.put(HTMLFormElement.class, "form");
    HTML_ELEMENTS.put(HTMLHeadingElement.class, "named");
    HTML_ELEMENTS.put(HTMLElement.class, "named");
    HTML_ELEMENTS.put(HTMLHRElement.class, "hr");
    HTML_ELEMENTS.put(HTMLImageElement.class, "img");
    HTML_ELEMENTS.put(HTMLInputElement.class, "input");
    HTML_ELEMENTS.put(HTMLLabelElement.class, "label");
    HTML_ELEMENTS.put(HTMLLegendElement.class, "legend");
    HTML_ELEMENTS.put(HTMLLIElement.class, "li");
    HTML_ELEMENTS.put(HTMLMapElement.class, "map");
    HTML_ELEMENTS.put(HTMLMenuElement.class, "menu");
    HTML_ELEMENTS.put(HTMLMenuItemElement.class, "menuitem");
    HTML_ELEMENTS.put(HTMLMeterElement.class, "meter");
    HTML_ELEMENTS.put(HTMLObjectElement.class, "object");
    HTML_ELEMENTS.put(HTMLOListElement.class, "ol");
    HTML_ELEMENTS.put(HTMLOptGroupElement.class, "optgroup");
    HTML_ELEMENTS.put(HTMLOptionElement.class, "option");
    HTML_ELEMENTS.put(HTMLOutputElement.class, "output");
    HTML_ELEMENTS.put(HTMLParagraphElement.class, "p");
    HTML_ELEMENTS.put(HTMLParamElement.class, "param");
    HTML_ELEMENTS.put(HTMLPreElement.class, "pre");
    HTML_ELEMENTS.put(HTMLProgressElement.class, "progress");
    HTML_ELEMENTS.put(HTMLQuoteElement.class, "q");
    HTML_ELEMENTS.put(HTMLScriptElement.class, "script");
    HTML_ELEMENTS.put(HTMLSelectElement.class, "select");
    HTML_ELEMENTS.put(HTMLSourceElement.class, "source");
    HTML_ELEMENTS.put(HTMLTableElement.class, "table");
    HTML_ELEMENTS.put(HTMLTableCellElement.class, "td");
    HTML_ELEMENTS.put(HTMLTextAreaElement.class, "textarea");
    HTML_ELEMENTS.put(HTMLTableRowElement.class, "tr");
    HTML_ELEMENTS.put(HTMLTrackElement.class, "track");
    HTML_ELEMENTS.put(HTMLUListElement.class, "ul");
    HTML_ELEMENTS.put(HTMLVideoElement.class, "video");
  }

  public Elemenatal2FactoryGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    HTML_ELEMENTS.keySet().forEach(clazz -> {
      iocContext.register(Inject.class, clazz, WiringElementType.FIELD_TYPE, this);
      iocContext.getBlacklist().add(clazz.getCanonicalName());
    });
  }

  @Override
  public void generateBeanFactory(ClassBuilder classBuilder, Definition definition) {

  }

  @Override
  public Expression generateBeanCall(ClassBuilder classBuilder, FieldPoint fieldPoint) {
    classBuilder.getClassCompilationUnit().addImport(DomGlobal.class);
    classBuilder.getClassCompilationUnit().addImport(InstanceImpl.class);
    classBuilder.getClassCompilationUnit().addImport(Provider.class);
    classBuilder.getClassCompilationUnit()
        .addImport(fieldPoint.getType().getQualifiedName().toString());

    // new InstanceImpl<>(() -> (HTMLDivElement) DomGlobal.document.createElement("div"));

    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(new MethodCallExpr(
        new FieldAccessExpr(new NameExpr(DomGlobal.class.getSimpleName()), "document"),
        "createElement").addArgument(getTagFromType(fieldPoint))));

    return new ObjectCreationExpr().setType(InstanceImpl.class).addArgument(lambda);
  }

  private StringLiteralExpr getTagFromType(FieldPoint fieldPoint) {
    if (fieldPoint.isNamed()) {
      return new StringLiteralExpr(fieldPoint.getNamed());
    }

    Class clazz;
    try {
      clazz = Class.forName(fieldPoint.getType().getQualifiedName().toString());
    } catch (ClassNotFoundException e) {
      throw new Error("Unable to process " + fieldPoint.getType().getQualifiedName().toString()
          + " " + e.getMessage());
    }

    if (!HTML_ELEMENTS.containsKey(clazz)) {
      throw new GenerationException(
          "Unable to process " + fieldPoint.getType().getQualifiedName().toString());
    }

    if (HTML_ELEMENTS.get(clazz).stream().findFirst().get().equals("named")) {
      throw new GenerationException(
          "Unable to process " + fieldPoint.getType().getQualifiedName().toString() + ", "
              + fieldPoint.getName() + " must be annotated with @Named(\"tag_name\")");
    }

    return new StringLiteralExpr(HTML_ELEMENTS.get(clazz).stream().findFirst().get());
  }
}
