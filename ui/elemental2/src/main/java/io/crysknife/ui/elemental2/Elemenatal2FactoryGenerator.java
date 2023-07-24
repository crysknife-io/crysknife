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

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.google.auto.common.MoreTypes;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

import static io.crysknife.ui.elemental2.ElmToTagMapping.HTML_ELEMENTS;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/7/19
 */
@Generator(priority = 100000)
public class Elemenatal2FactoryGenerator extends IOCGenerator<BeanDefinition> {

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
  public String generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {
    return generationUtils.wrapCallInstanceImpl(new MethodCallExpr(
        new FieldAccessExpr(new NameExpr(DomGlobal.class.getCanonicalName()), "document"),
        "createElement").addArgument(getTagFromType(fieldPoint))).toString();
  }

  private StringLiteralExpr getTagFromType(InjectableVariableDefinition fieldPoint) {
    if (fieldPoint.getVariableElement().getAnnotation(Named.class) != null
        && !fieldPoint.getVariableElement().getAnnotation(Named.class).value().equals("")) {
      return new StringLiteralExpr(fieldPoint.getVariableElement().getAnnotation(Named.class)
          .value().toLowerCase(Locale.ROOT));
    }

    Class<? extends HTMLElement> clazz;
    try {
      clazz = (Class<? extends HTMLElement>) Class.forName(MoreTypes
          .asTypeElement(fieldPoint.getVariableElement().asType()).getQualifiedName().toString());
    } catch (ClassNotFoundException e) {
      throw new Error(
          "Unable to process " + MoreTypes.asTypeElement(fieldPoint.getVariableElement().asType())
              .getQualifiedName().toString() + " " + e.getMessage());
    }

    if (!HTML_ELEMENTS.containsKey(clazz)) {
      throw new GenerationException("Unable to process " + MoreTypes
          .asTypeElement(fieldPoint.getVariableElement().asType()).getQualifiedName().toString());
    }

    long count = HTML_ELEMENTS.get(clazz).size();

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

  public static Set<Map.Entry<Class<? extends HTMLElement>, String>> getHTMLElementByTag(
      String tag) {
    return HTML_ELEMENTS.entries().stream().filter(elm -> elm.getValue().equals(tag))
        .collect(Collectors.toSet());
  }

}
