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

/**
 * Add template translation, if translation service added
 */
package io.crysknife.ui.templates.generator.translation;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.templates.generator.TemplatedGeneratorUtils;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.NodeVisitor;

import java.util.HashSet;
import java.util.Set;

public class TranslationServiceGenerator {

  private final static String TRANSLATION_SERVICE =
      "io.crysknife.ui.translation.api.spi.TranslationService";
  private final static String DATA_I18N_KEY = "data-i18n-key";
  private final IOCContext iocContext;
  private boolean isEnabled;
  private TemplatedGeneratorUtils templatedGeneratorUtils;

  public TranslationServiceGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;

    templatedGeneratorUtils = new TemplatedGeneratorUtils(iocContext);

    try {
      Class.forName(TRANSLATION_SERVICE);
      isEnabled = true;
    } catch (ClassNotFoundException e) {
      isEnabled = false;
    }
  }

  public void process(ClassBuilder builder, TemplateContext context) {
    if (isEnabled) {
      String html = context.getRoot().getInnerHtml();
      if (html == null || html.isEmpty()) {
        return;
      }
      Document document = Jsoup.parse(html);
      I18NKeyVisitor i18NKeyVisitor = new I18NKeyVisitor();
      org.jsoup.select.NodeTraversor.traverse(i18NKeyVisitor, document);
      if (!i18NKeyVisitor.result.isEmpty()) {
        addGetI18nValue(builder);
        addI18nTranslationCall(builder, context);
      }
    }
  }

  private void addI18nTranslationCall(ClassBuilder builder, TemplateContext context) {
    LambdaExpr lambda = new LambdaExpr().setEnclosingParameters(true);
    lambda.getParameters().add(new Parameter().setName("s").setType("String"));
    lambda.setBody(new ExpressionStmt(new MethodCallExpr("getI18nValue").addArgument("s")));

    builder.getInitInstanceMethod().getBody().get().addAndGetStatement(
        new MethodCallExpr(new NameExpr("io.crysknife.client.utils.dom.DomVisit"), "visit")
            .addArgument(templatedGeneratorUtils.getInstanceCallExpression(context))
            .addArgument(new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType()
                    .setName("io.crysknife.ui.templates.client.TemplateTranslationVisitor"))
                .addArgument(new StringLiteralExpr(getI18nPrefix(context.getTemplateFileName())))
                .addArgument(lambda)));

  }

  private void addGetI18nValue(ClassBuilder builder) {
    builder.addFieldWithInitializer(TRANSLATION_SERVICE, "translationService",
        new NameExpr(TRANSLATION_SERVICE + "Impl.INSTANCE"), Modifier.Keyword.PRIVATE);

    MethodDeclaration method = builder.addMethod("getI18nValue", Modifier.Keyword.PRIVATE);
    method.addParameter(
        new Parameter(new ClassOrInterfaceType().setName("String"), "translationKey"));
    method.setType("String");
    method.getBody().get().addAndGetStatement(
        new ReturnStmt(new MethodCallExpr(new NameExpr("translationService"), "getTranslation")
            .addArgument("translationKey")));
  }

  private static class I18NKeyVisitor implements NodeVisitor {

    private Set<Element> result = new HashSet<>();

    @Override
    public void head(org.jsoup.nodes.Node node, int i) {
      if (node.hasAttr(DATA_I18N_KEY) && !node.attr(DATA_I18N_KEY).isEmpty()) {
        result.add((Element) node);
      }
    }

    @Override
    public void tail(org.jsoup.nodes.Node node, int i) {

    }
  }

  public static String getI18nPrefix(final String templateFile) {
    final int idx1 = templateFile.lastIndexOf('/');
    final int idx2 = templateFile.lastIndexOf('.');
    return templateFile.substring(idx1 + 1, idx2 + 1);
  }
}
