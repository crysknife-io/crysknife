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

import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.client.utils.dom.DomVisit;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.templates.client.TemplateTranslationVisitor;
import io.crysknife.ui.templates.generator.TemplatedGeneratorUtils;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class TranslationServiceGenerator {

  private final static String TRANSLATION_SERVICE =
      "io.crysknife.ui.translation.api.spi.TranslationService";
  private final static String DATA_I18N_KEY = "data-i18n-key";
  private boolean isEnabled;
  private TemplatedGeneratorUtils templatedGeneratorUtils;

  public TranslationServiceGenerator(IOCContext iocContext) {
    templatedGeneratorUtils = new TemplatedGeneratorUtils(iocContext);

    try {
      Class.forName(TRANSLATION_SERVICE);
      isEnabled = true;
    } catch (ClassNotFoundException e) {
      isEnabled = false;
    }
  }

  public void process(ClassMetaInfo builder, TemplateContext context) {
    if (isEnabled) {
      String html = context.getRoot().getInnerHtml();
      if (html == null || html.isEmpty()) {
        return;
      }
      Document document = Jsoup.parse(html);
      I18NKeyVisitor i18NKeyVisitor = new I18NKeyVisitor();
      NodeTraversor.traverse(i18NKeyVisitor, document);
      if (!i18NKeyVisitor.result.isEmpty()) {
        addGetI18nValue(builder);
        addI18nTranslationCall(builder, context);
      }
    }
  }

  private void addI18nTranslationCall(ClassMetaInfo builder, TemplateContext context) {
    LambdaExpr lambda = new LambdaExpr().setEnclosingParameters(true);
    lambda.getParameters().add(new Parameter().setName("s").setType("String"));
    lambda.setBody(new ExpressionStmt(new MethodCallExpr("getI18nValue").addArgument("s")));

    builder.addToDoInitInstance(
        () -> new MethodCallExpr(new NameExpr(DomVisit.class.getCanonicalName()), "visit")
            .addArgument(templatedGeneratorUtils.getInstanceCallExpression(context))
            .addArgument(new ObjectCreationExpr()
                .setType(new ClassOrInterfaceType()
                    .setName(TemplateTranslationVisitor.class.getCanonicalName()))
                .addArgument(new StringLiteralExpr(getI18nPrefix(context.getTemplateFileName())))
                .addArgument(lambda))
            .toString());
  }

  private void addGetI18nValue(ClassMetaInfo builder) {
    String translationService =
        "private io.crysknife.ui.translation.api.spi.TranslationService translationService = new io.crysknife.ui.translation.api.spi.TranslationServiceImpl.INSTANCE;";
    builder.addToBody(() -> translationService);

    String getI18nValue =
        "private String getI18nValue(String translationKey) { return translationService.getTranslation(translationKey); }";
    builder.addToBody(() -> getI18nValue);
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
