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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import elemental2.dom.*;
import io.crysknife.annotation.Generator;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.BeanIOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.Definition;
import io.crysknife.logger.TreeLogger;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/7/19
 */
@Generator(priority = 100000)
public class Elemenatal2FactoryGenerator extends BeanIOCGenerator<BeanDefinition> {

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
    HTML_ELEMENTS.put(HTMLDListElement.class, "dl");
    HTML_ELEMENTS.put(HTMLEmbedElement.class, "embed");
    HTML_ELEMENTS.put(HTMLFieldSetElement.class, "fieldset");
    HTML_ELEMENTS.put(HTMLFormElement.class, "form");
    HTML_ELEMENTS.put(HTMLHeadingElement.class, "h1");
    HTML_ELEMENTS.put(HTMLHeadingElement.class, "h2");
    HTML_ELEMENTS.put(HTMLHeadingElement.class, "h3");
    HTML_ELEMENTS.put(HTMLHeadingElement.class, "h4");
    HTML_ELEMENTS.put(HTMLHeadingElement.class, "h5");
    HTML_ELEMENTS.put(HTMLHeadingElement.class, "h6");
    HTML_ELEMENTS.put(HTMLElement.class, "");
    HTML_ELEMENTS.put(HTMLElement.class, "span");
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

    HTML_ELEMENTS.put(HTMLTableSectionElement.class, "thead");
    HTML_ELEMENTS.put(HTMLTableSectionElement.class, "tfoot");
    HTML_ELEMENTS.put(HTMLTableSectionElement.class, "tbody");
  }

  public Elemenatal2FactoryGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    HTML_ELEMENTS.keySet().forEach(clazz -> {
      iocContext.register(Inject.class, clazz, WiringElementType.FIELD_TYPE, this);
      iocContext.getBuildIn().add(clazz.getCanonicalName());
    });
  }

  @Override
  public void generate(ClassBuilder clazz, BeanDefinition beanDefinition) {

  }

  @Override
  public Expression generateBeanLookupCall(ClassBuilder classBuilder,
      InjectableVariableDefinition fieldPoint) {
    classBuilder.getClassCompilationUnit().addImport(DomGlobal.class);
    classBuilder.getClassCompilationUnit().addImport(MoreTypes
        .asTypeElement(fieldPoint.getVariableElement().asType()).getQualifiedName().toString());
    return generationUtils.wrapCallInstanceImpl(classBuilder,
        new MethodCallExpr(
            new FieldAccessExpr(new NameExpr(DomGlobal.class.getSimpleName()), "document"),
            "createElement").addArgument(getTagFromType(fieldPoint)));
  }

  private StringLiteralExpr getTagFromType(InjectableVariableDefinition fieldPoint) {
    if (fieldPoint.getVariableElement().getAnnotation(Named.class) != null
        && !fieldPoint.getVariableElement().getAnnotation(Named.class).value().equals("")) {
      return new StringLiteralExpr(fieldPoint.getVariableElement().getAnnotation(Named.class)
          .value().toLowerCase(Locale.ROOT));
    }

    Class clazz;
    try {
      clazz = Class.forName(MoreTypes.asTypeElement(fieldPoint.getVariableElement().asType())
          .getQualifiedName().toString());
    } catch (ClassNotFoundException e) {
      throw new Error(
          "Unable to process " + MoreTypes.asTypeElement(fieldPoint.getVariableElement().asType())
              .getQualifiedName().toString() + " " + e.getMessage());
    }

    if (!HTML_ELEMENTS.containsKey(clazz)) {
      throw new GenerationException("Unable to process " + MoreTypes
          .asTypeElement(fieldPoint.getVariableElement().asType()).getQualifiedName().toString());
    }

    long count = HTML_ELEMENTS.get(clazz).stream().count();

    if (count > 1) {
      throw new GenerationException("Unable to process "
          + MoreTypes.asTypeElement(fieldPoint.getVariableElement().asType()).getQualifiedName()
              .toString()
          + ", "
          + MoreTypes.asTypeElement(fieldPoint.getVariableElement().asType()).getEnclosingElement()
          + "." + fieldPoint.getVariableElement().getSimpleName()
          + " must be annotated with @Named(\"tag_name\")");
    }

    return new StringLiteralExpr(HTML_ELEMENTS.get(clazz).stream().findFirst().get());
  }

  public static Set<Map.Entry<Class, String>> getHTMLElementByTag(String tag) {
    return HTML_ELEMENTS.entries().stream().filter(elm -> elm.getValue().equals(tag))
        .collect(Collectors.toSet());
  }

  @Override
  public void generate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {
    throw new UnsupportedOperationException();
  }
}
