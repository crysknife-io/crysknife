/*
 * Copyright © 2023 Treblereel
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

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.gwt.elemento.processor.AbortProcessingException;
import org.jboss.gwt.elemento.processor.ExpressionParser;
import org.jboss.gwt.elemento.processor.TemplateSelector;
import org.jboss.gwt.elemento.processor.context.RootElementInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;

public class TemplateParser {

    private static final Escaper JAVA_STRING_ESCAPER =
            Escapers.builder().addEscape('"', "\\\"").addEscape('\n', "").addEscape('\r', "").build();

    private final IOCContext iocContext;
    private final TreeLogger logger;

    TemplateParser(IOCContext iocContext, TreeLogger logger) {
        this.iocContext = iocContext;
        this.logger = logger;
    }

    TemplateSelector getTemplateSelector(TypeElement type, Templated templated) {
        boolean hasValue = Strings.emptyToNull(templated.value()) != null;
        boolean hasInline = Strings.emptyToNull(templated.inline()) != null;

        if (hasValue && hasInline) {
            abortWithError(type,
                    "@Templated must not specify both value() and inline() at the same time");
        }

        if (hasInline) {
            return TemplateSelector.ofInline(templated.inline());
        }

        if (!hasValue) {
            return new TemplateSelector(type.getSimpleName().toString() + ".html");
        }

        if (templated.value().contains("#")) {
            Iterator<String> iterator = Splitter.on('#').limit(2).omitEmptyStrings().trimResults()
                    .split(templated.value()).iterator();
            return new TemplateSelector(iterator.next(), iterator.next());
        }
        return new TemplateSelector(templated.value());
    }

    org.jsoup.nodes.Element parseTemplate(TypeElement type, TemplateSelector templateSelector) {
        if (templateSelector.inline) {
            return parseInlineTemplate(type, templateSelector.template);
        }
        return parseFileTemplate(type, templateSelector);
    }

    private org.jsoup.nodes.Element parseInlineTemplate(TypeElement type, String html) {
        Document document = Jsoup.parse(html);
        if (document.body() == null || document.body().children().isEmpty()) {
            abortWithError(type, "No content found in inline template");
            return null;
        }
        return document.body().children().first();
    }

    private org.jsoup.nodes.Element parseFileTemplate(TypeElement type,
                                                      TemplateSelector templateSelector) {
        org.jsoup.nodes.Element root = null;
        String fqTemplate =
                MoreElements.getPackage(type).getQualifiedName().toString().replace('.', '/') + "/"
                        + templateSelector.template;

        try {
            URL url = iocContext.getGenerationContext().getResourceOracle().findResource(type,
                    templateSelector.template);
            if (url == null) {
                abortWithError(type, "Cannot find template \"%s\". Please make sure the template exists.",
                        fqTemplate);
            }
            String html = new String(url.openStream().readAllBytes(), Charset.defaultCharset());
            Document document = Jsoup.parse(html);
            if (templateSelector.hasSelector()) {
                org.jsoup.nodes.Element rootElement = getRoot(document, templateSelector.selector);
                if (rootElement == null) {
                    abortWithError(type, "Unable to select HTML from \"%s\" using \"%s\"",
                            templateSelector.template, "[data-field] || [id]");
                } else {
                    root = rootElement;
                }
            } else {
                if (document.body() == null || document.body().children().isEmpty()) {
                    abortWithError(type, "No content found in the <body> of \"%s\"",
                            templateSelector.template);
                } else {
                    root = document.body().children().first();
                }
            }
        } catch (IOException e) {
            abortWithError(type, "Unable to read template \"%s\": %s", fqTemplate, e.getMessage());
        }
        return root;
    }

    RootElementInfo createRootElementInfo(org.jsoup.nodes.Element root, String subclass) {
        List<Attribute> attributes = root.attributes().asList().stream()
                .filter(attribute -> !attribute.getKey().equals("data-field")).collect(Collectors.toList());

        java.util.Optional<Attribute> dataField = root.attributes().asList().stream()
                .filter(attribute -> attribute.getKey().equals("data-field")).findFirst();

        ExpressionParser expressionParser = new ExpressionParser();
        String html = root.children().isEmpty() ? null : JAVA_STRING_ESCAPER.escape(root.html());
        Map<String, String> expressions = expressionParser.parse(html);
        expressions.putAll(expressionParser.parse(root.outerHtml()));

        return new RootElementInfo(root.tagName(), dataField, subclass.toLowerCase() + "_root_element",
                attributes, html, expressions);
    }

    String getIsElementTypeParameter(List<? extends TypeMirror> interfaces) {
        String typeParam = HTMLElement.class.getCanonicalName();
        for (TypeMirror interfaceMirror : interfaces) {
            if (MoreTypes.isTypeOf(IsElement.class, interfaceMirror)) {
                DeclaredType interfaceDeclaration = MoreTypes.asDeclared(interfaceMirror);
                List<? extends TypeMirror> typeArguments = interfaceDeclaration.getTypeArguments();
                if (!typeArguments.isEmpty()) {
                    TypeElement typeArgument =
                            (TypeElement) iocContext.getGenerationContext().getProcessingEnvironment()
                                    .getTypeUtils().asElement(typeArguments.get(0));
                    return typeArgument.getQualifiedName().toString();
                }
            }
        }
        return typeParam;
    }

    private org.jsoup.nodes.Element getRoot(Document document, String selector) {
        RootNodeVisitor visitor = new RootNodeVisitor(selector);
        org.jsoup.select.NodeTraversor.traverse(visitor, document);
        return visitor.result;
    }

    private void abortWithError(javax.lang.model.element.Element element, String msg, Object... args) {
        String message = "Error at " + element.getEnclosingElement() + "." + element.getSimpleName()
                + " : " + String.format(msg, args);
        logger.log(TreeLogger.Type.ERROR, message);
        throw new AbortProcessingException();
    }
}
