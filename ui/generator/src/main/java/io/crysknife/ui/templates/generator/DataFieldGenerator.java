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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Strings;
import elemental2.dom.HTMLElement;
import io.crysknife.client.Reflect;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.security.IfRole;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.generator.dto.TemplateDefinition;
import io.crysknife.util.TypeUtils;
import jsinterop.base.Js;
import org.jboss.gwt.elemento.processor.AbortProcessingException;
import org.jboss.gwt.elemento.processor.TemplateSelector;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.jsoup.select.Elements;
import org.treblereel.j2cl.processors.utils.J2CLUtils;

import static java.util.stream.Collectors.joining;

public class DataFieldGenerator {

    private static final String QUOTE = "\"";

    private final IOCContext iocContext;
    private final J2CLUtils j2CLUtils;
    private final ProcessingEnvironment processingEnvironment;
    private final TemplatedGeneratorUtils templatedGeneratorUtils;
    private final TreeLogger logger;

    DataFieldGenerator(IOCContext iocContext, J2CLUtils j2CLUtils,
                       TemplatedGeneratorUtils templatedGeneratorUtils, TreeLogger logger) {
        this.iocContext = iocContext;
        this.j2CLUtils = j2CLUtils;
        this.processingEnvironment = iocContext.getGenerationContext().getProcessingEnvironment();
        this.templatedGeneratorUtils = templatedGeneratorUtils;
        this.logger = logger;
    }

    List<DataElementInfo> discoverDataFields(TypeElement type, TemplateSelector templateSelector,
                                              org.jsoup.nodes.Element root) {
        List<DataElementInfo> dataElements = new ArrayList<>();

        TypeUtils
                .getAnnotatedElements(iocContext.getGenerationContext().getElements(), type,
                        DataField.class)
                .stream().filter(e -> e.getKind().isField()).map(MoreElements::asVariable)
                .forEach(field -> {
                    if (field.getModifiers().contains(Modifier.STATIC)) {
                        abortWithError(field, "@%s member must not be static", DataField.class.getSimpleName());
                    }
                    DataElementInfo.Kind kind =
                            templatedGeneratorUtils.getDataElementInfoKind(field.asType());
                    if (kind == DataElementInfo.Kind.Custom) {
                        warning(field, "Unknown type %s. Consider using one of %s.", field.asType(),
                                EnumSet.complementOf(EnumSet.of(DataElementInfo.Kind.Custom)));
                    }

                    String selector = getSelector(field);
                    verifySelector(selector, field, templateSelector, root);

                    String typeName = MoreTypes.asTypeElement(field.asType()).getQualifiedName().toString();
                    if (kind == DataElementInfo.Kind.HTMLElement) {
                        verifyHTMLElement(typeName, selector, field, templateSelector, root);
                    }

                    String strategy = getStrategy(field);
                    dataElements.add(new DataElementInfo(field, selector, kind, strategy));
                });
        return dataElements;
    }

    void generateCode(TemplateContext templateContext, TemplateDefinition templateDefinition) {
        Expression instance = new NameExpr("instance");
        instance = new MethodCallExpr(instance, "getElement");

        for (DataElementInfo element : templateContext.getDataElements()) {
            MethodCallExpr resolveElement;
            MethodCallExpr fieldAccessCallExpr = getFieldAccessCallExpr(element);

            IfStmt ifStmt = new IfStmt().setCondition(
                    new BinaryExpr(fieldAccessCallExpr, new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));

            if (element.needsCast()) {
                resolveElement = new MethodCallExpr(
                        new ClassOrInterfaceType().setName("TemplateUtil").getNameAsExpression(),
                        "resolveElementAs")
                        .setTypeArguments(new ClassOrInterfaceType().setName(element.getType().toString()))
                        .addArgument(instance).addArgument(new StringLiteralExpr(element.getSelector()));
            } else {
                resolveElement = new MethodCallExpr(
                        new ClassOrInterfaceType().setName("TemplateUtil").getNameAsExpression(),
                        "resolveElement").addArgument(instance)
                        .addArgument(new StringLiteralExpr(element.getName()));
            }
            String mangleName = j2CLUtils.getVariableMangledName(element.getField());
            MethodCallExpr fieldSetCallExpr =
                    new MethodCallExpr(
                            new MethodCallExpr(new NameExpr(Js.class.getSimpleName()), "asPropertyMap")
                                    .addArgument("instance"),
                            "set").addArgument(
                            new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                                    .addArgument(new StringLiteralExpr(mangleName)).addArgument("instance"));

            ifStmt.setThenStmt(
                    new BlockStmt().addAndGetStatement(fieldSetCallExpr.addArgument(resolveElement)));
            ifStmt.setElseStmt(new BlockStmt().addAndGetStatement(new MethodCallExpr(
                    new ClassOrInterfaceType().setName("TemplateUtil").getNameAsExpression(),
                    "replaceElement").addArgument(instance)
                    .addArgument(new StringLiteralExpr(element.getSelector()))
                    .addArgument(getInstanceByElementKind(element, fieldAccessCallExpr))));

            boolean isElemento =
                    element.getKind() == DataElementInfo.Kind.ElementoIsElement;
            boolean isCrysknifeIsElement =
                    element.getKind() == DataElementInfo.Kind.IsElement;
            boolean useBean = "USE_BEAN".equals(element.getStrategy());
            List<String> roles = getIfRoleValues(element.getField());
            io.crysknife.ui.templates.generator.dto.Element elementDto =
                    new io.crysknife.ui.templates.generator.dto.Element(element.getSelector(), mangleName,
                            element.getType().toString(), element.needsCast(), isElemento,
                            isCrysknifeIsElement, useBean, roles);

            templateDefinition.getElements().add(elementDto);
        }
    }

    MethodCallExpr getFieldAccessCallExpr(DataElementInfo info) {
        return getFieldAccessCallExpr(info.getField());
    }

    MethodCallExpr getFieldAccessCallExpr(VariableElement field) {
        String mangleName = j2CLUtils.getVariableMangledName(field);

        return new MethodCallExpr(
                new MethodCallExpr(new NameExpr(Js.class.getSimpleName()), "asPropertyMap")
                        .addArgument("instance"),
                "get").addArgument(
                new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                        .addArgument(new StringLiteralExpr(mangleName)).addArgument("instance"));
    }

    Expression getInstanceByElementKind(DataElementInfo element, Expression instance) {
        if (element.getKind().equals(DataElementInfo.Kind.IsElement)) {
            instance = new MethodCallExpr(
                    new EnclosedExpr(new CastExpr(new ClassOrInterfaceType()
                            .setName(io.crysknife.client.IsElement.class.getCanonicalName()), instance)),
                    "getElement");
        }

        return new EnclosedExpr(new CastExpr(
                new ClassOrInterfaceType().setName(HTMLElement.class.getCanonicalName()), instance));
    }

    private void verifyHTMLElement(String htmlType, String selector, Element element,
                                   TemplateSelector templateSelector, org.jsoup.nodes.Element root) {
        Set<String> tags = Elemental2TagMapping.HTML_ELEMENTS.get(htmlType);
        if (!tags.isEmpty()) {
            Elements elements = root.getElementsByAttributeValue("data-field", selector);
            if (!elements.isEmpty()) {
                String tagName = elements.get(0).tagName().toLowerCase();
                if (!tags.contains(tagName)) {
                    String fieldOrMethod = element instanceof VariableElement ? "field" : "method";
                    String expected = tags.size() == 1 ? QUOTE + tags.iterator().next() + QUOTE
                            : "one of " + tags.stream().map(t -> QUOTE + t + QUOTE).collect(joining(", "));
                    abortWithError(element,
                            "The %s maps to the wrong HTML element: Expected %s, but found \"%s\" in %s using \"[data-field=%s]\" as selector.",
                            fieldOrMethod, expected, tagName, templateSelector, selector);
                }
            }
        }
    }

    private void verifySelector(String selector, Element element, TemplateSelector templateSelector,
                                org.jsoup.nodes.Element root) {
        if (root.getElementById(selector) != null) {
            return;
        }

        Elements elements = root.getElementsByAttributeValue("data-field", selector);
        long matchCount = elements.stream()
                .filter(elem -> elem.attributes().getIgnoreCase("data-element").equals(selector)).count();

        if (elements.isEmpty() && matchCount == 0) {
            abortWithError(element,
                    "Cannot find a matching element in %s using \"[data-field=%s]\" as selector",
                    templateSelector, selector);
        } else if (matchCount > 1) {
            warning(element,
                    "Found %d matching elements in %s using \"[data-field=%s]\" as selector. Only the first will be used.",
                    elements.size(), templateSelector, selector);
        }
    }

    private String getSelector(Element element) {
        String selector = getAnnotationValue(element, "value");
        return Strings.emptyToNull(selector) == null ? element.getSimpleName().toString() : selector;
    }

    private String getStrategy(Element element) {
        String strategy = getAnnotationValue(element, "strategy");
        return Strings.emptyToNull(strategy) == null ? "USE_TEMPLATE" : strategy;
    }

    private String getAnnotationValue(Element element, String methodName) {
        Optional<AnnotationMirror> annotationMirror =
                MoreElements.getAnnotationMirror(element, DataField.class).toJavaUtil();
        if (annotationMirror.isPresent()) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> values = processingEnvironment
                    .getElementUtils().getElementValuesWithDefaults(annotationMirror.get());
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
                if (entry.getKey().getSimpleName().toString().equals(methodName)) {
                    return String.valueOf(entry.getValue().getValue());
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<String> getIfRoleValues(VariableElement field) {
        Optional<AnnotationMirror> annotationMirror =
                MoreElements.getAnnotationMirror(field, IfRole.class).toJavaUtil();
        if (annotationMirror.isPresent()) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> values = processingEnvironment
                    .getElementUtils().getElementValuesWithDefaults(annotationMirror.get());
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
                if (entry.getKey().getSimpleName().toString().equals("value")) {
                    List<? extends AnnotationValue> roleValues =
                            (List<? extends AnnotationValue>) entry.getValue().getValue();
                    return roleValues.stream()
                            .map(v -> String.valueOf(v.getValue()))
                            .collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }

    private void abortWithError(Element element, String msg, Object... args) {
        String message = "Error at " + element.getEnclosingElement() + "." + element.getSimpleName()
                + " : " + String.format(msg, args);
        logger.log(TreeLogger.Type.ERROR, message);
        throw new AbortProcessingException();
    }

    private void warning(Element element, String msg, Object... args) {
        logger.log(TreeLogger.Type.WARN, "Warning at " + element.getEnclosingElement() + "."
                + element.getSimpleName() + " : " + String.format(msg, args));
    }
}
