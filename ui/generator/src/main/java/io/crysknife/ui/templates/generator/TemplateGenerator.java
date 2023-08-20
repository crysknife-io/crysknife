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
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.inet.lib.less.Less;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.client.Reflect;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.helpers.FreemarkerTemplateGenerator;
import io.crysknife.generator.helpers.MethodCallGenerator;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.common.client.injectors.StyleInjector;
import io.crysknife.ui.templates.client.EventHandlerHolder;
import io.crysknife.ui.templates.client.EventHandlerRegistration;
import io.crysknife.ui.templates.client.TemplateUtil;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.SinkNative;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.templates.generator.dto.Event;
import io.crysknife.ui.templates.generator.dto.TemplateDefinition;
import io.crysknife.ui.templates.generator.events.EventHandlerTemplatedProcessor;
import io.crysknife.ui.templates.generator.events.EventHandlerValidator;
import io.crysknife.ui.templates.generator.translation.TranslationServiceGenerator;
import io.crysknife.util.TypeUtils;
import jsinterop.base.Js;
import org.apache.commons.io.IOUtils;
import org.jboss.gwt.elemento.processor.AbortProcessingException;
import org.jboss.gwt.elemento.processor.ExpressionParser;
import org.jboss.gwt.elemento.processor.TemplateSelector;
import org.jboss.gwt.elemento.processor.TypeSimplifier;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.EventHandlerInfo;
import org.jboss.gwt.elemento.processor.context.RootElementInfo;
import org.jboss.gwt.elemento.processor.context.StyleSheet;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.treblereel.j2cl.processors.utils.J2CLUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.auto.common.MoreTypes.asElement;
import static java.util.stream.Collectors.joining;

@Generator
public class TemplateGenerator extends IOCGenerator<BeanDefinition> {

  private static final String QUOTE = "\"";
  private static final Escaper JAVA_STRING_ESCAPER =
      Escapers.builder().addEscape('"', "\\\"").addEscape('\n', "").addEscape('\r', "").build();
  private final TypeElement isElement;
  private final J2CLUtils j2CLUtils;
  private final TemplateValidator templateValidator;
  private final ProcessingEnvironment processingEnvironment;
  private final DataFieldProcessor dataFieldProcessor;
  private final EventHandlerTemplatedProcessor eventHandlerTemplatedProcessor;
  private final TranslationServiceGenerator translationServiceGenerator;
  private final TemplatedGeneratorUtils templatedGeneratorUtils;

  private final EventHandlerValidator eventHandlerValidator;

  private final MethodCallGenerator methodCallGenerator;

  private final FreemarkerTemplateGenerator freemarkerTemplateGenerator =
      new FreemarkerTemplateGenerator("ui.ftlh");

  public TemplateGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
    this.j2CLUtils = new J2CLUtils(iocContext.getGenerationContext().getProcessingEnvironment());
    this.processingEnvironment = iocContext.getGenerationContext().getProcessingEnvironment();
    this.dataFieldProcessor = new DataFieldProcessor(iocContext, treeLogger);
    this.templateValidator = new TemplateValidator(iocContext);
    this.eventHandlerTemplatedProcessor = new EventHandlerTemplatedProcessor(iocContext);
    this.translationServiceGenerator = new TranslationServiceGenerator(iocContext);
    this.templatedGeneratorUtils = new TemplatedGeneratorUtils(iocContext);
    this.eventHandlerValidator = new EventHandlerValidator(iocContext);
    this.methodCallGenerator = new MethodCallGenerator(iocContext);
    this.isElement = iocContext.getGenerationContext().getProcessingEnvironment().getElementUtils()
        .getTypeElement(IsElement.class.getCanonicalName());
  }

  @Override
  public void register() {
    iocContext.register(Templated.class, WiringElementType.CLASS_DECORATOR, this);
  }

  @Override
  public void generate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {

    try {
      templateValidator.validate(MoreTypes.asTypeElement(beanDefinition.getType()));
    } catch (UnableToCompleteException e) {
      throw new GenerationException(e);
    }

    TemplateDefinition templateDefinition = new TemplateDefinition();

    maybeHasNotGetMethod(beanDefinition, templateDefinition);
    classMetaInfo.addToDoCreateInstance(() -> "setAndInitTemplate()");
    setAndInitTemplate(classMetaInfo, beanDefinition, templateDefinition);
  }

  private void maybeHasNotGetMethod(BeanDefinition beanDefinition,
      TemplateDefinition templateDefinition) {
    if (!hasGetElement(beanDefinition)) {
      Optional<ExecutableElement> executableElement = ElementFilter
          .methodsIn(isElement.getEnclosedElements()).stream().map(MoreElements::asExecutable)
          .filter(method -> method.getSimpleName().toString().equals("getElement"))
          .filter(method -> method.getModifiers().contains(Modifier.DEFAULT)).findFirst();

      if (executableElement.isPresent()) {
        templateDefinition.setInitRootElement(true);
        String mangledName =
            j2CLUtils.createDeclarationMethodDescriptor(executableElement.get()).getMangledName();
        templateDefinition.setRootElementPropertyName(mangledName);
      }
    }
  }

  private void setAndInitTemplate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition,
      TemplateDefinition templateDefinition) {
    TypeElement type = MoreTypes.asTypeElement(beanDefinition.getType());
    String isElementTypeParameter = getIsElementTypeParameter(type.getInterfaces());
    Templated templated = type.getAnnotation(Templated.class);

    TemplateContext context = new TemplateContext(TypeSimplifier.packageNameOf(type),
        TypeSimplifier.classNameOf(type), type.toString(), isElementTypeParameter, type.asType());
    // root element and template
    TemplateSelector templateSelector = getTemplateSelector(type, templated);

    // TODO warning, this must be refactored, coz template could have another package
    String fqTemplate =
        TypeSimplifier.packageNameOf(type).replace('.', '/') + "/" + templateSelector.template;
    context.setTemplateFileName(fqTemplate);

    org.jsoup.nodes.Element root = parseTemplate(type, templateSelector);
    context.setRoot(createRootElementInfo(root, type.toString()));

    // find and verify all @DataField members
    List<DataElementInfo> dataElements = processDataElements(type, templateSelector, root);
    context.setDataElements(dataFieldProcessor.process(dataElements, context, root));

    List<EventHandlerInfo> eventElements =
        eventHandlerTemplatedProcessor.processEventHandlers(type, context);
    context.setEvents(eventElements);

    // css/gss stylesheet
    context.setStylesheet(getStylesheet(type, templated));

    // generate code
    code(classMetaInfo, beanDefinition, context, templateDefinition);

    // maybe add translation
    translationServiceGenerator.process(classMetaInfo, context);
    String source = freemarkerTemplateGenerator.toSource(templateDefinition);
    classMetaInfo.addToBody(() -> source);
    logger.log(TreeLogger.Type.INFO, "Generated templated implementation [" + context.getSubclass()
        + "] for [" + context.getBase() + "]");
  }

  private void code(ClassMetaInfo builder, BeanDefinition beanDefinition,
      TemplateContext templateContext, TemplateDefinition templateDefinition) {
    addImports(builder);
    setStylesheet(builder, templateContext, templateDefinition);

    setAttributes(templateContext, templateDefinition);
    setInnerHTML(templateContext, templateDefinition);

    processDataFields(templateContext, templateDefinition);
    processEventHandlers(beanDefinition, templateContext, templateDefinition);
    processOnDestroy(builder);
  }

  private void addImports(ClassMetaInfo builder) {
    builder.addImport(DomGlobal.class);
    builder.addImport(Js.class);
    builder.addImport(Reflect.class);
    builder.addImport(TemplateUtil.class);
    builder.addImport(EventListener.class);
    builder.addImport(EventHandlerHolder.class);
    builder.addImport(EventHandlerRegistration.class);
  }

  private void setInnerHTML(TemplateContext templateContext,
      TemplateDefinition templateDefinition) {
    if (templateContext.getRoot().getInnerHtml() != null
        && !templateContext.getRoot().getInnerHtml().isEmpty()) {
      templateDefinition.setHtml(templateContext.getRoot().getInnerHtml());
    }
  }

  private void processDataFields(TemplateContext templateContext,
      TemplateDefinition templateDefinition) {
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
      String mangleName = j2CLUtils.createFieldDescriptor(element.getField()).getMangledName();
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

      io.crysknife.ui.templates.generator.dto.Element elementDto =
          new io.crysknife.ui.templates.generator.dto.Element(element.getSelector(), mangleName,
              element.getType().toString(), element.needsCast());

      templateDefinition.getElements().add(elementDto);

    }
  }

  private void processEventHandlers(BeanDefinition beanDefinition, TemplateContext templateContext,
      TemplateDefinition templateDefinition) {

    for (EventHandlerInfo eventHandlerInfo : templateContext.getEvents()) {
      try {
        eventHandlerValidator.validate(eventHandlerInfo.getMethod());
        if (MoreElements.isAnnotationPresent(eventHandlerInfo.getMethod(), SinkNative.class)) {
          throw new GenerationException(
              String.format("Method %s annotated with @SinkNative must be static",
                  eventHandlerInfo.getMethod().getSimpleName()));
        } else {
          String[] eventTypes = eventHandlerInfo.getMethod().getParameters().get(0)
              .getAnnotation(ForEvent.class).value();
          String clazz = iocContext.getGenerationContext().getTypes()
              .erasure(eventHandlerInfo.getMethod().getParameters().get(0).asType()).toString();
          String mangleName = j2CLUtils.createFieldDescriptor(eventHandlerInfo.getInfo().getField(),
              beanDefinition.getType()).getMangledName();
          String call = methodCallGenerator.generate(beanDefinition.getType(),
              eventHandlerInfo.getMethod(), List.of("e"));
          Event event = new Event(eventTypes, mangleName, clazz, call);
          templateDefinition.getEvents().add(event);
        }
      } catch (UnableToCompleteException e) {
        throw new GenerationException(e);
      }
    }
  }

  private void processOnDestroy(ClassMetaInfo builder) {
    builder.addToOnDestroy((Supplier<String>) () -> "eventHandlerRegistration.clear(instance);");
  }

  private void setStylesheet(ClassMetaInfo builder, TemplateContext templateContext,
      TemplateDefinition templateDefinition) {
    if (templateContext.getStylesheet() != null) {
      builder.addImport(StyleInjector.class);
      if (templateContext.getStylesheet().isLess()) {
        try {
          String less =
              IOUtils.toString(templateContext.getStylesheet().getFile(), Charset.defaultCharset());
          Less.compile(null, less, false);
          final String compiledCss = Less.compile(null, less, false);
          templateDefinition.setCss(templatedGeneratorUtils.escape(compiledCss));
        } catch (IOException e) {
          throw new GenerationException(
              "Unable to process Less " + templateContext.getStylesheet());
        }
      } else {
        try {
          String css =
              IOUtils.toString(templateContext.getStylesheet().getFile(), Charset.defaultCharset());
          templateDefinition.setCss(templatedGeneratorUtils.escape(css));
        } catch (IOException e) {
          throw new GenerationException(
              "Unable to process Css/Gss :" + templateContext.getStylesheet(), e);
        }
      }
    }
  }

  private StyleSheet getStylesheet(TypeElement type, Templated templated) {
    if (Strings.emptyToNull(templated.stylesheet()) == null) {
      List<String> postfixes = Arrays.asList(".css", ".gss", ".less");
      for (String postfix : postfixes) {
        String beanName = type.getSimpleName().toString() + postfix;
        URL file = iocContext.getGenerationContext().getResourceOracle()
            .findResource(MoreElements.getPackage(type), beanName);
        if (file != null) {
          return new StyleSheet(type.getSimpleName() + postfix, file);
        }
      }
    } else {
      try {
        URL url = iocContext.getGenerationContext().getResourceOracle()
            .findResource(MoreElements.getPackage(type), templated.stylesheet());
        if (url != null) {
          return new StyleSheet(templated.stylesheet(), url);
        }
      } catch (IllegalArgumentException e1) {

      }
      throw new GenerationException(
          String.format("Unable to find stylesheet defined at %s", type.getQualifiedName()));
    }

    return null;
  }

  // ------------------------------------------------------ root element / template
  private RootElementInfo createRootElementInfo(org.jsoup.nodes.Element root, String subclass) {
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

  private TemplateSelector getTemplateSelector(TypeElement type, Templated templated) {
    if (Strings.emptyToNull(templated.value()) == null) {
      return new TemplateSelector(type.getSimpleName().toString() + ".html");
    } else {
      if (templated.value().contains("#")) {
        Iterator<String> iterator = Splitter.on('#').limit(2).omitEmptyStrings().trimResults()
            .split(templated.value()).iterator();
        return new TemplateSelector(iterator.next(), iterator.next());
      } else {
        return new TemplateSelector(templated.value());
      }
    }
  }

  private void setAttributes(TemplateContext templateContext,
      TemplateDefinition templateDefinition) {
    templateContext.getRoot().getAttributes().stream()
        .map(attr -> new io.crysknife.ui.templates.generator.dto.Attribute(attr.getKey(),
            attr.getValue()))
        .forEach(a -> templateDefinition.getAttributes().add(a));
  }

  // ------------------------------------------------------ @DataElement fields and methods
  private List<DataElementInfo> processDataElements(TypeElement type,
      TemplateSelector templateSelector, org.jsoup.nodes.Element root) {
    List<DataElementInfo> dataElements = new ArrayList<>();

    // fields
    TypeUtils
        .getAnnotatedElements(iocContext.getGenerationContext().getElements(), type,
            DataField.class)
        .stream().filter(e -> e.getKind().isField()).map(MoreElements::asVariable)
        .forEach(field -> {
          // verify the field
          if (field.getModifiers().contains(Modifier.STATIC)) {
            abortWithError(field, "@%s member must not be static", DataField.class.getSimpleName());
          }
          DataElementInfo.Kind kind =
              templatedGeneratorUtils.getDataElementInfoKind(field.asType());
          if (kind == DataElementInfo.Kind.Custom) {
            warning(field, "Unknown type %s. Consider using one of %s.", field.asType(),
                EnumSet.complementOf(EnumSet.of(DataElementInfo.Kind.Custom)));
          }

          // verify the selector
          String selector = getSelector(field);
          verifySelector(selector, field, templateSelector, root);

          // verify the HTMLElement type
          String typeName = MoreTypes.asTypeElement(field.asType()).getQualifiedName().toString();
          if (kind == DataElementInfo.Kind.HTMLElement) {
            verifyHTMLElement(typeName, selector, field, templateSelector, root);
          }

          // create info class for template processing
          dataElements.add(new DataElementInfo(field, selector, kind));
        });
    return dataElements;
  }

  private void verifyHTMLElement(String htmlType, String selector, Element element,
      TemplateSelector templateSelector, org.jsoup.nodes.Element root) {
    // make sure the HTMLElement subtype maps to the right HTML tag
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
    // make sure to use the same logic for finding matching elements as in TemplateUtils!
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

  public Expression getInstanceByElementKind(DataElementInfo element, Expression instance) {
    if (element.getKind().equals(DataElementInfo.Kind.IsElement)) {
      instance = new MethodCallExpr(
          new EnclosedExpr(new CastExpr(new ClassOrInterfaceType()
              .setName(io.crysknife.client.IsElement.class.getCanonicalName()), instance)),
          "getElement");
    }

    return new EnclosedExpr(new CastExpr(
        new ClassOrInterfaceType().setName(HTMLElement.class.getCanonicalName()), instance));
  }

  public MethodCallExpr getFieldAccessCallExpr(DataElementInfo info) {
    return getFieldAccessCallExpr(info.getField());
  }

  public MethodCallExpr getFieldAccessCallExpr(VariableElement field) {
    String mangleName = j2CLUtils.createFieldDescriptor(field).getMangledName();

    return new MethodCallExpr(
        new MethodCallExpr(new NameExpr(Js.class.getSimpleName()), "asPropertyMap")
            .addArgument("instance"),
        "get").addArgument(
            new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                .addArgument(new StringLiteralExpr(mangleName)).addArgument("instance"));
  }

  private String getSelector(Element element) {
    String selector = null;
    Optional<AnnotationMirror> annotationMirror =
        MoreElements.getAnnotationMirror(element, DataField.class).toJavaUtil();
    if (annotationMirror.isPresent()) {
      Map<? extends ExecutableElement, ? extends AnnotationValue> values = processingEnvironment
          .getElementUtils().getElementValuesWithDefaults(annotationMirror.get());
      if (!values.isEmpty()) {
        selector = String.valueOf(values.values().iterator().next().getValue());
      }
    }
    return Strings.emptyToNull(selector) == null ? element.getSimpleName().toString() : selector;
  }

  private String getIsElementTypeParameter(List<? extends TypeMirror> interfaces) {
    String typeParam = HTMLElement.class.getCanonicalName();
    for (TypeMirror interfaceMirror : interfaces) {
      if (MoreTypes.isTypeOf(IsElement.class, interfaceMirror)) {
        DeclaredType interfaceDeclaration = MoreTypes.asDeclared(interfaceMirror);
        List<? extends TypeMirror> typeArguments = interfaceDeclaration.getTypeArguments();
        if (!typeArguments.isEmpty()) {
          TypeElement typeArgument =
              (TypeElement) processingEnvironment.getTypeUtils().asElement(typeArguments.get(0));
          return typeArgument.getQualifiedName().toString();
        }
      }
    }
    return typeParam;
  }

  private org.jsoup.nodes.Element parseTemplate(TypeElement type,
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
      String html = IOUtils.toString(url, Charset.defaultCharset());
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

  private org.jsoup.nodes.Element getRoot(Document document, String selector) {
    RootNodeVisitor visitor = new RootNodeVisitor(selector);
    org.jsoup.select.NodeTraversor.traverse(visitor, document);
    return visitor.result;
  }

  private boolean hasGetElement(BeanDefinition beanDefinition) {
    return javax.lang.model.util.ElementFilter
        .methodsIn(asElement(beanDefinition.getType()).getEnclosedElements()).stream()
        .map(MoreElements::asExecutable)
        .filter(method -> method.getSimpleName().toString().equals("getElement"))
        .anyMatch(method -> method.getParameters().isEmpty());
  }

  /**
   * Issue a compilation error and abandon the processing of this template. This does not prevent
   * the processing of other templates.
   */
  public void abortWithError(Element element, String msg, Object... args) {
    error(element, msg, args);
    throw new AbortProcessingException();
  }

  public void warning(Element element, String msg, Object... args) {
    logger.log(TreeLogger.Type.WARN, "Warning at " + element.getEnclosingElement() + "."
        + element.getSimpleName() + " : " + String.format(msg, args));
  }

  public void error(Element element, String msg, Object... args) {
    String message = "Error at " + element.getEnclosingElement() + "." + element.getSimpleName()
        + " : " + String.format(msg, args);
    logger.log(TreeLogger.Type.ERROR, message);
    throw new GenerationException();
  }

}
