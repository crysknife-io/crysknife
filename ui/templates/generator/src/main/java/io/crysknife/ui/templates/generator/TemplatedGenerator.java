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

package io.crysknife.ui.templates.generator;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.inet.lib.less.Less;
import elemental2.dom.*;
import io.crysknife.annotation.Generator;
import io.crysknife.client.Reflect;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectionParameterDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.templates.client.StyleInjector;
import io.crysknife.ui.templates.client.TemplateUtil;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.templates.generator.events.EventHandlerGenerator;
import io.crysknife.ui.templates.generator.events.EventHandlerTemplatedProcessor;
import io.crysknife.ui.templates.generator.translation.TranslationServiceGenerator;
import io.crysknife.util.Utils;
import jsinterop.base.Js;
import org.apache.commons.io.IOUtils;
import org.jboss.elemento.IsElement;
import org.jboss.gwt.elemento.processor.*;
import org.jboss.gwt.elemento.processor.context.StyleSheet;
import org.jboss.gwt.elemento.processor.context.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/7/19
 */

// TODO refactor this class
@Generator
public class TemplatedGenerator extends IOCGenerator<BeanDefinition> {

  private static final String QUOTE = "\"";
  private static final Escaper JAVA_STRING_ESCAPER =
      Escapers.builder().addEscape('"', "\\\"").addEscape('\n', "").addEscape('\r', "").build();
  // List of elements from https://developer.mozilla.org/en-US/docs/Web/HTML/Element
  // which have a class in elemental2.dom, are standardized and not obsolete or deprecated
  private static final SetMultimap<String, String> HTML_ELEMENTS = HashMultimap.create();

  static {
    HTML_ELEMENTS.put(HTMLAnchorElement.class.getName(), "a");
    HTML_ELEMENTS.put(HTMLAreaElement.class.getName(), "area");
    HTML_ELEMENTS.put(HTMLAudioElement.class.getName(), "audio");
    HTML_ELEMENTS.put(HTMLQuoteElement.class.getName(), "blockquote");
    HTML_ELEMENTS.put(HTMLBRElement.class.getName(), "br");
    HTML_ELEMENTS.put(HTMLButtonElement.class.getName(), "button");
    HTML_ELEMENTS.put(HTMLCanvasElement.class.getName(), "canvas");
    HTML_ELEMENTS.put(HTMLTableCaptionElement.class.getName(), "caption");
    HTML_ELEMENTS.put(HTMLTableColElement.class.getName(), "col");
    HTML_ELEMENTS.put(HTMLDataListElement.class.getName(), "datalist");
    HTML_ELEMENTS.put(HTMLDetailsElement.class.getName(), "details");
    HTML_ELEMENTS.put(HTMLDialogElement.class.getName(), "dialog");
    HTML_ELEMENTS.put(HTMLDivElement.class.getName(), "div");
    HTML_ELEMENTS.put(HTMLEmbedElement.class.getName(), "embed");
    HTML_ELEMENTS.put(HTMLFieldSetElement.class.getName(), "fieldset");
    HTML_ELEMENTS.put(HTMLFormElement.class.getName(), "form");
    HTML_ELEMENTS.putAll(HTMLHeadingElement.class.getName(),
        asList("h1", "h2", "h3", "h4", "h5", "h6"));
    HTML_ELEMENTS.put(HTMLHRElement.class.getName(), "hr");
    HTML_ELEMENTS.put(HTMLImageElement.class.getName(), "img");
    HTML_ELEMENTS.put(HTMLInputElement.class.getName(), "input");
    HTML_ELEMENTS.put(HTMLLabelElement.class.getName(), "label");
    HTML_ELEMENTS.put(HTMLLegendElement.class.getName(), "legend");
    HTML_ELEMENTS.put(HTMLLIElement.class.getName(), "li");
    HTML_ELEMENTS.put(HTMLMapElement.class.getName(), "map");
    HTML_ELEMENTS.put(HTMLMenuElement.class.getName(), "menu");
    HTML_ELEMENTS.put(HTMLMenuItemElement.class.getName(), "menuitem");
    HTML_ELEMENTS.put(HTMLMeterElement.class.getName(), "meter");
    HTML_ELEMENTS.put(HTMLObjectElement.class.getName(), "object");
    HTML_ELEMENTS.put(HTMLOListElement.class.getName(), "ol");
    HTML_ELEMENTS.put(HTMLOptGroupElement.class.getName(), "optgroup");
    HTML_ELEMENTS.put(HTMLOptionElement.class.getName(), "option");
    HTML_ELEMENTS.put(HTMLOutputElement.class.getName(), "output");
    HTML_ELEMENTS.put(HTMLParagraphElement.class.getName(), "p");
    HTML_ELEMENTS.put(HTMLParamElement.class.getName(), "param");
    HTML_ELEMENTS.put(HTMLPreElement.class.getName(), "pre");
    HTML_ELEMENTS.put(HTMLProgressElement.class.getName(), "progress");
    HTML_ELEMENTS.put(HTMLQuoteElement.class.getName(), "q");
    HTML_ELEMENTS.put(HTMLScriptElement.class.getName(), "script");
    HTML_ELEMENTS.put(HTMLSelectElement.class.getName(), "select");
    HTML_ELEMENTS.put(HTMLSourceElement.class.getName(), "source");
    HTML_ELEMENTS.put(HTMLTableElement.class.getName(), "table");
    HTML_ELEMENTS.put(HTMLTableCellElement.class.getName(), "td");
    HTML_ELEMENTS.put(HTMLTableCellElement.class.getName(), "th");
    HTML_ELEMENTS.put(HTMLTextAreaElement.class.getName(), "textarea");
    HTML_ELEMENTS.put(HTMLTableRowElement.class.getName(), "tr");
    HTML_ELEMENTS.put(HTMLTrackElement.class.getName(), "track");
    HTML_ELEMENTS.put(HTMLUListElement.class.getName(), "ul");
    HTML_ELEMENTS.put(HTMLVideoElement.class.getName(), "video");

  }

  private ProcessingEnvironment processingEnvironment;
  private Messager messager;
  private BeanDefinition beanDefinition;
  private TemplatedGeneratorUtils templatedGeneratorUtils;
  private EventHandlerTemplatedProcessor eventHandlerTemplatedProcessor;
  private TranslationServiceGenerator translationServiceGenerator;
  private DataFieldProcessor dataFieldProcessor;
  private EventHandlerGenerator eventHandlerGenerator;

  public TemplatedGenerator(IOCContext iocContext) {
    super(iocContext);
    templatedGeneratorUtils = new TemplatedGeneratorUtils(iocContext);
    eventHandlerTemplatedProcessor = new EventHandlerTemplatedProcessor(iocContext);
    eventHandlerGenerator = new EventHandlerGenerator(iocContext, this);
    dataFieldProcessor = new DataFieldProcessor(iocContext);
    translationServiceGenerator = new TranslationServiceGenerator(iocContext);
  }

  @Override
  public void register() {
    this.processingEnvironment = iocContext.getGenerationContext().getProcessingEnvironment();
    this.messager = processingEnvironment.getMessager();

    iocContext.register(Templated.class, WiringElementType.CLASS_DECORATOR, this);

    if (templatedGeneratorUtils.isWidgetType != null) {
      new TemplateWidgetGenerator(iocContext).build(false).generate();
    }
  }

  @Override
  public void generate(ClassBuilder builder, BeanDefinition beanDefinition) {
    this.beanDefinition = beanDefinition;
    validateType(MoreTypes.asTypeElement(beanDefinition.getType()),
        MoreTypes.asTypeElement(beanDefinition.getType()).getAnnotation(Templated.class));
    processType(builder, MoreTypes.asTypeElement(beanDefinition.getType()),
        MoreTypes.asTypeElement(beanDefinition.getType()).getAnnotation(Templated.class));
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

  private StyleSheet getStylesheet(TypeElement type, Templated templated) {
    if (Strings.emptyToNull(templated.stylesheet()) == null) {
      List<String> postfixes = Arrays.asList(".css", ".gss", ".less");
      String path = MoreElements.getPackage(type).toString().replaceAll("\\.", "/");
      for (String postfix : postfixes) {
        String beanName = type.getSimpleName().toString() + postfix;
        URL file = iocContext.getGenerationContext().getResourceOracle()
            .findResource(path + "/" + beanName);
        if (file != null) {
          return new StyleSheet(type.getSimpleName() + postfix, file);
        }
      }
    } else {
      try {
        String path =
            MoreElements.getPackage(type).getQualifiedName().toString().replaceAll("\\.", "/") + "/"
                + templated.stylesheet();

        URL url = iocContext.getGenerationContext().getResourceOracle().findResource(path);
        if (url != null) {
          return new StyleSheet(templated.stylesheet(), url);
        }
      } catch (IllegalArgumentException e1) {
        String path =
            MoreElements.getPackage(type).getQualifiedName().toString().replaceAll("\\.", "/") + "/"
                + templated.stylesheet();

        throw new GenerationException(path, e1);
      }
      throw new GenerationException(
          String.format("Unable to find stylesheet defined at %s", type.getQualifiedName()));
    }

    return null;
  }

  private void processType(ClassBuilder builder, TypeElement type, Templated templated) {
    String isElementTypeParameter = getIsElementTypeParameter(type.getInterfaces());
    String subclass = TypeSimplifier.simpleNameOf(generatedClassName(type, "Templated_", ""));
    TemplateContext context = new TemplateContext(TypeSimplifier.packageNameOf(type),
        TypeSimplifier.classNameOf(type), subclass, isElementTypeParameter, type.asType());
    // root element and template
    TemplateSelector templateSelector = getTemplateSelector(type, templated);

    // TODO warning, this must be refactored, coz template could have another package
    String fqTemplate =
        org.jboss.gwt.elemento.processor.TypeSimplifier.packageNameOf(type).replace('.', '/') + "/"
            + templateSelector.template;
    context.setTemplateFileName(fqTemplate);

    org.jsoup.nodes.Element root = parseTemplate(type, templateSelector);
    context.setRoot(createRootElementInfo(root, subclass));


    // find and verify all @DataField members
    List<DataElementInfo> dataElements = processDataElements(type, templateSelector, root);
    context.setDataElements(dataFieldProcessor.process(dataElements, context, root));

    List<EventHandlerInfo> eventElements =
        eventHandlerTemplatedProcessor.processEventHandlers(type, context);
    context.setEvents(eventElements);

    // css/gss stylesheet
    context.setStylesheet(getStylesheet(type, templated));

    // generate code
    code(builder, context);

    // maybe add translation
    translationServiceGenerator.process(builder, context);

    info("Generated templated implementation [%s] for [%s]", context.getSubclass(),
        context.getBase());
  }

  private void code(ClassBuilder builder, TemplateContext templateContext) {
    addImports(builder);
    generateWrapper(builder, templateContext);
    setStylesheet(builder, templateContext);

    addInitTemplateCallMethod(builder);
    maybeInitWidgets(builder, templateContext);

    processDataFields(builder, templateContext);
    // maybeInitWidgets(builder, templateContext);
    processEventHandlers(builder, templateContext);
  }


  private void maybeInitWidgets(ClassBuilder builder, TemplateContext templateContext) {
    DataElementInfo.Kind kind =
        templatedGeneratorUtils.getDataElementInfoKind(templateContext.getDataElementType());

    List<DataElementInfo> widgets = templateContext.getDataElements().stream()
        .filter(elm -> elm.getKind().equals(DataElementInfo.Kind.IsWidget))
        .collect(Collectors.toList());
    if (kind.equals(DataElementInfo.Kind.IsWidget) || !widgets.isEmpty()) {
      builder.getClassCompilationUnit().addImport(List.class);
      builder.getClassCompilationUnit().addImport(ArrayList.class);

      ExpressionStmt expressionStmt = new ExpressionStmt();
      VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();

      VariableDeclarator variableDeclarator = new VariableDeclarator();
      variableDeclarator.setName("widgets");

      ClassOrInterfaceType list = new ClassOrInterfaceType().setName(List.class.getSimpleName());
      ClassOrInterfaceType arrayList =
          new ClassOrInterfaceType().setName(ArrayList.class.getSimpleName());
      list.setTypeArguments(
          new ClassOrInterfaceType().setName("org.gwtproject.user.client.ui.Widget"));

      variableDeclarator.setType(list);
      variableDeclarator.setInitializer(new ObjectCreationExpr().setType(arrayList));
      variableDeclarationExpr.getVariables().add(variableDeclarator);

      expressionStmt.setExpression(variableDeclarationExpr);

      builder.getInitInstanceMethod().getBody().get().addAndGetStatement(expressionStmt);

      widgets.forEach(widget -> {
        TypeElement typeElement =
            iocContext.getGenerationContext().getElements().getTypeElement(widget.getType());

        boolean isWidget = iocContext.getGenerationContext().getTypes()
            .isSubtype(typeElement.asType(), templatedGeneratorUtils.widgetType.asType());
        MethodCallExpr uncheckedCast =
            new MethodCallExpr(new NameExpr(Js.class.getCanonicalName()), "uncheckedCast")
                .addArgument(getFieldAccessCallExpr(widget.getName()));

        if (!isWidget) {
          uncheckedCast.setTypeArguments(
              new ClassOrInterfaceType().setName(templatedGeneratorUtils.isWidgetType.toString()));
          uncheckedCast = new MethodCallExpr(uncheckedCast, "asWidget");
        }

        MethodCallExpr methodCallExpr =
            new MethodCallExpr(new NameExpr("widgets"), "add").addArgument(uncheckedCast);
        builder.getInitInstanceMethod().getBody().get().addAndGetStatement(methodCallExpr);
      });

      Expression instance = templatedGeneratorUtils.getInstanceMethodName(kind);

      MethodCallExpr doInit;

      if (kind.equals(DataElementInfo.Kind.IsWidget)) {
        doInit = new MethodCallExpr(new ClassOrInterfaceType()
            .setName("org.gwtproject.user.client.ui.TemplateWidget").getNameAsExpression(),
            "initTemplated").addArgument(new NameExpr("instance"))
                .addArgument(new FieldAccessExpr(new NameExpr("instance"), "root"))
                .addArgument("widgets");
      } else {
        doInit = new MethodCallExpr(new ClassOrInterfaceType()
            .setName("org.gwtproject.user.client.ui.TemplateWidget").getNameAsExpression(),
            "initTemplated")
                .addArgument(Js.class.getCanonicalName() + ".uncheckedCast(" + instance + ")")
                .addArgument("widgets");
      }

      builder.getInitInstanceMethod().getBody().get().addAndGetStatement(doInit);
    }
  }


  private void generateWrapper(ClassBuilder builder, TemplateContext templateContext) {
    ClassOrInterfaceDeclaration wrapper = new ClassOrInterfaceDeclaration();
    wrapper.setName(MoreTypes.asTypeElement(beanDefinition.getType()).getSimpleName().toString());
    wrapper.addExtendedType(beanDefinition.getQualifiedName());
    wrapper.setModifier(com.github.javaparser.ast.Modifier.Keyword.FINAL, true);
    String element = getElementFromTag(templateContext);

    wrapper.addField(element, "root", com.github.javaparser.ast.Modifier.Keyword.PRIVATE);

    ConstructorDeclaration constructor =
        wrapper.addConstructor(com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
    if (beanDefinition.getConstructorParams() != null) {
      List<String> args = new LinkedList<>();
      for (InjectionParameterDefinition argument : beanDefinition.getConstructorParams()) {
        constructor.addAndGetParameter(MoreTypes
            .asTypeElement(argument.getVariableElement().asType()).getQualifiedName().toString(),
            argument.getVariableElement().getSimpleName().toString());
        args.add(argument.getVariableElement().getSimpleName().toString());
      }
      StringJoiner joiner = new StringJoiner(",");
      args.stream().forEach(joiner::add);
      constructor.getBody().addStatement("super(" + joiner + ");");
    }

    DataElementInfo.Kind dataElementInfo =
        templatedGeneratorUtils.getDataElementInfoKind(beanDefinition.getType());

    addElementMethod(wrapper, templateContext, dataElementInfo);
    addInitMethod(wrapper, templateContext, dataElementInfo);

    addInitMethodCaller(builder);

    builder.getClassDeclaration().addMember(wrapper);
  }

  private void addInitMethodCaller(ClassBuilder builder) {
    MethodDeclaration doInitInstance =
        builder.addMethod("doInitInstance", com.github.javaparser.ast.Modifier.Keyword.PROTECTED);
    doInitInstance.addAnnotation(Override.class);
    doInitInstance.addParameter(new Parameter(
        new ClassOrInterfaceType().setName(beanDefinition.getType().toString()), "instance"));
    doInitInstance.getBody().get().addAndGetStatement(
        new MethodCallExpr("doInitInstance").addArgument(new EnclosedExpr(new CastExpr(
            new ClassOrInterfaceType().setName(Utils.getSimpleClassName(beanDefinition.getType())),
            new NameExpr("instance")))));
  }

  private void addElementMethod(ClassOrInterfaceDeclaration wrapper,
      TemplateContext templateContext, DataElementInfo.Kind kind) {

    String element = getElementFromTag(templateContext);
    Expression root;
    MethodDeclaration method = wrapper.addMethod(templatedGeneratorUtils.getMethodName(kind),
        com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
    if (!kind.equals(DataElementInfo.Kind.IsWidget)) {
      method.addAnnotation(Override.class);
      method.setType(element);
      root = new FieldAccessExpr(new ThisExpr(), "root");
    } else {
      method.setType(HTMLElement.class.getCanonicalName());
      root = templatedGeneratorUtils.uncheckedCastCall(asWidgetGetElementCall(new ThisExpr()),
          HTMLElement.class.getCanonicalName());

    }

    ReturnStmt _return = new ReturnStmt(root);

    method.getBody().get().addStatement(_return);
  }

  private Expression asWidgetCall(Expression target) {
    return new MethodCallExpr(target, "asWidget");
  }

  private Expression asWidgetGetElementCall(Expression target) {
    return new MethodCallExpr(asWidgetCall(target), "getElement");
  }

  private void addInitMethod(ClassOrInterfaceDeclaration wrapper, TemplateContext templateContext,
      DataElementInfo.Kind kind) {
    String element = getElementFromTag(templateContext);

    MethodDeclaration method = wrapper.addMethod("_setAndInitTemplate",
        com.github.javaparser.ast.Modifier.Keyword.PRIVATE);

    MethodCallExpr methodCallExpr = getDataFieldFieldAccessCallExpr(templateContext);

    Expression root = new FieldAccessExpr(new ThisExpr(), "root");
    method.getBody().get().addStatement(new AssignExpr().setTarget(root).setValue(
        new MethodCallExpr(new NameExpr("Js"), "uncheckedCast").addArgument(methodCallExpr)));

    setAttributes(method.getBody().get(), templateContext);
    setInnerHTML(method.getBody().get(), templateContext);
  }

  private MethodCallExpr getDataFieldFieldAccessCallExpr(TemplateContext templateContext) {
    if (templateContext.getRoot().getDataField().isPresent()) {
      String dataField = templateContext.getRoot().getDataField().get().getValue();
      java.util.Optional<DataElementInfo> dataElementInfo = templateContext.getDataElements()
          .stream().filter(e -> e.getSelector().equals(dataField)).findFirst();
      if (dataElementInfo.isPresent()) {
        MethodCallExpr expr = getFieldAccessCallExpr(dataElementInfo.get().getName());
        return expr;
      }
    }
    return new MethodCallExpr(
        new FieldAccessExpr(new NameExpr("elemental2.dom.DomGlobal"), "document"), "createElement")
            .addArgument(new StringLiteralExpr(templateContext.getRoot().getTag()));
  }

  private void setStylesheet(ClassBuilder builder, TemplateContext templateContext) {
    if (templateContext.getStylesheet() != null) {
      builder.getClassCompilationUnit().addImport(StyleInjector.class);

      if (!templateContext.getStylesheet().isLess()) {
        /*
         * builder.getClassCompilationUnit().addImport(CssResource.class);
         * builder.getClassCompilationUnit().addImport(CssResource.NotStrict.class);
         * builder.getClassCompilationUnit().addImport(Resource.class);
         * builder.getClassCompilationUnit().addImport(ClientBundle.Source.class);
         *
         * ClassOrInterfaceDeclaration inner = new ClassOrInterfaceDeclaration();
         * inner.setName("Stylesheet"); inner.setInterface(true); inner.addAnnotation("Resource");
         *
         * new JavaParser().parseBodyDeclaration("@Source(\"" +
         * templateContext.getStylesheet().getStyle() +
         * "\") @NotStrict CssResource getStyle();").ifSuccessful(inner::addMember);
         * builder.getClassDeclaration().addMember(inner);
         *
         * String theName = Utils.getFactoryClassName(beanDefinition.getType()) + "_StylesheetImpl";
         */

        String css;
        try {
          css =
              IOUtils.toString(templateContext.getStylesheet().getFile(), Charset.defaultCharset());
        } catch (IOException e) {
          throw new GenerationException(
              "Unable to process Css/Gss :" + templateContext.getStylesheet(), e);
        }

        // TODO Temporary workaround, till gwt-dom StyleInjector ll be fixed
        builder.getInitInstanceMethod().getBody().get()
            .addStatement(new MethodCallExpr(new MethodCallExpr(
                new ClassOrInterfaceType().setName("StyleInjector").getNameAsExpression(),
                "fromString").addArgument(new StringLiteralExpr(escape(css))), "inject"));
      } else {
        try {
          String less =
              IOUtils.toString(templateContext.getStylesheet().getFile(), Charset.defaultCharset());
          Less.compile(null, less, false);
          final String compiledCss = Less.compile(null, less, false);
          builder.getInitInstanceMethod().getBody().get()
              .addStatement(new MethodCallExpr(
                  new MethodCallExpr(
                      new ClassOrInterfaceType().setName("StyleInjector").getNameAsExpression(),
                      "fromString").addArgument(new StringLiteralExpr(escape(compiledCss))),
                  "inject"));
        } catch (IOException e) {
          throw new GenerationException(
              "Unable to process Less " + templateContext.getStylesheet());
        }
      }
    }
  }

  private void addInitTemplateCallMethod(ClassBuilder builder) {
    builder.addField(MoreTypes.asTypeElement(beanDefinition.getType()).getSimpleName().toString(),
        "instance", com.github.javaparser.ast.Modifier.Keyword.PRIVATE);

    builder.getInitInstanceMethod().getBody().get()
        .addAndGetStatement(new MethodCallExpr(new NameExpr("instance"), "_setAndInitTemplate"));
  }

  private void processDataFields(ClassBuilder builder, TemplateContext templateContext) {
    Expression instance = templatedGeneratorUtils.getInstanceCallExpression(templateContext);

    templateContext.getDataElements().forEach(element -> {
      MethodCallExpr resolveElement;
      MethodCallExpr fieldAccessCallExpr = getFieldAccessCallExpr(element.getName());

      IfStmt ifStmt = new IfStmt().setCondition(
          new BinaryExpr(fieldAccessCallExpr, new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));


      if (element.needsCast()) {
        resolveElement = new MethodCallExpr(
            new ClassOrInterfaceType().setName("TemplateUtil").getNameAsExpression(),
            "resolveElementAs")
                .setTypeArguments(new ClassOrInterfaceType().setName(element.getType()))
                .addArgument(instance).addArgument(new StringLiteralExpr(element.getSelector()));
      } else {
        resolveElement = new MethodCallExpr(
            new ClassOrInterfaceType().setName("TemplateUtil").getNameAsExpression(),
            "resolveElement").addArgument(instance)
                .addArgument(new StringLiteralExpr(element.getName()));
      }

      MethodCallExpr fieldSetCallExpr = null;
      if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.GWT2)) {
        fieldSetCallExpr = new MethodCallExpr(
            new NameExpr(
                MoreTypes.asTypeElement(beanDefinition.getType()).getQualifiedName() + "Info"),
            element.getName()).addArgument("instance");
      } else if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.J2CL)) {
        fieldSetCallExpr = new MethodCallExpr(
            new MethodCallExpr(new NameExpr(Js.class.getSimpleName()), "asPropertyMap")
                .addArgument("instance"),
            "set").addArgument(
                new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                    .addArgument(new StringLiteralExpr(
                        Utils.getJsFieldName(getVariableElement(element.getName()))))
                    .addArgument("instance"));
      }

      ifStmt.setThenStmt(
          new BlockStmt().addAndGetStatement(fieldSetCallExpr.addArgument(resolveElement)));
      ifStmt.setElseStmt(new BlockStmt().addAndGetStatement(new MethodCallExpr(
          new ClassOrInterfaceType().setName("TemplateUtil").getNameAsExpression(),
          "replaceElement").addArgument(instance)
              .addArgument(new StringLiteralExpr(element.getSelector()))
              .addArgument(getInstanceByElementKind(element, fieldAccessCallExpr))));
      builder.getInitInstanceMethod().getBody().get().addAndGetStatement(ifStmt);
    });
  }

  public MethodCallExpr getFieldAccessCallExpr(String fieldName) {
    VariableElement field = getVariableElement(fieldName);
    return getFieldAccessCallExpr(field);
  }

  public MethodCallExpr getFieldAccessCallExpr(VariableElement field) {
    if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.GWT2)) {
      return new MethodCallExpr(
          new NameExpr(
              MoreTypes.asTypeElement(beanDefinition.getType()).getQualifiedName() + "Info"),
          field.getSimpleName().toString()).addArgument("instance");
    }

    return new MethodCallExpr(
        new MethodCallExpr(new NameExpr(Js.class.getSimpleName()), "asPropertyMap")
            .addArgument("instance"),
        "get").addArgument(
            new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                .addArgument(new StringLiteralExpr(Utils.getJsFieldName(field)))
                .addArgument("instance"));
  }

  // TODO this method must be refactored
  public Expression getInstanceByElementKind(DataElementInfo element, Expression instance) {
    if (element.getKind().equals(DataElementInfo.Kind.ElementoIsElement)) {
      instance = new MethodCallExpr(
          new EnclosedExpr(new CastExpr(
              new ClassOrInterfaceType().setName(IsElement.class.getCanonicalName()), instance)),
          "element");
    } else if (element.getKind().equals(DataElementInfo.Kind.CrysknifeIsElement)) {
      instance = new MethodCallExpr(
          new EnclosedExpr(new CastExpr(new ClassOrInterfaceType()
              .setName(io.crysknife.client.IsElement.class.getCanonicalName()), instance)),
          "getElement");
    } else if (element.getKind().equals(DataElementInfo.Kind.IsWidget)) {
      TypeElement typeElement =
          iocContext.getGenerationContext().getElements().getTypeElement(element.getType());
      boolean isIsWidget = iocContext.getGenerationContext().getTypes()
          .isSubtype(typeElement.asType(), templatedGeneratorUtils.isWidgetType.asType());
      Expression expr;
      if (isIsWidget) {
        expr =
            new MethodCallExpr(
                new EnclosedExpr(new CastExpr(new ClassOrInterfaceType()
                    .setName(templatedGeneratorUtils.isWidgetType.toString()), instance)),
                "asWidget");
      } else {
        expr = new EnclosedExpr(new CastExpr(
            new ClassOrInterfaceType().setName("org.gwtproject.user.client.ui.UIObject"),
            instance));
      }
      return new MethodCallExpr(new NameExpr(Js.class.getCanonicalName()),
          "<" + HTMLElement.class.getCanonicalName() + ">uncheckedCast")
              .addArgument(new MethodCallExpr(expr, "getElement"));
    }

    return new EnclosedExpr(new CastExpr(
        new ClassOrInterfaceType().setName(HTMLElement.class.getCanonicalName()), instance));
  }

  public VariableElement getVariableElement(String elementName) {
    return Utils
        .getAllFieldsIn(iocContext.getGenerationContext().getElements(),
            MoreTypes.asTypeElement(beanDefinition.getType()))
        .stream().filter(elm -> elm.getSimpleName().toString().equals(elementName))
        .map(elm -> MoreElements.asVariable(elm)).findFirst()
        .orElseThrow(() -> new Error("Unable to find @DataField " + elementName + " in "
            + MoreTypes.asTypeElement(beanDefinition.getType()).getQualifiedName()));
  }


  private void processEventHandlers(ClassBuilder builder, TemplateContext templateContext) {
    eventHandlerGenerator.generate(builder, templateContext);
  }

  private void addImports(ClassBuilder builder) {
    builder.getClassCompilationUnit().addImport(DomGlobal.class);
    builder.getClassCompilationUnit().addImport(TemplateUtil.class);
    builder.getClassCompilationUnit().addImport(Js.class);
    builder.getClassCompilationUnit().addImport(Reflect.class);
  }

  private void setInnerHTML(BlockStmt block, TemplateContext templateContext) {

    if (templateContext.getRoot().getInnerHtml() != null
        && !templateContext.getRoot().getInnerHtml().isEmpty()) {

      block.addAndGetStatement(new AssignExpr()
          .setTarget(new FieldAccessExpr(new FieldAccessExpr(new ThisExpr(), "root"), "innerHTML"))
          .setValue(new StringLiteralExpr(templateContext.getRoot().getInnerHtml())));
    }
  }

  private void setAttributes(BlockStmt block, TemplateContext templateContext) {
    templateContext.getRoot().getAttributes()
        .forEach(attribute -> block.addAndGetStatement(
            new MethodCallExpr(new FieldAccessExpr(new ThisExpr(), "root"), "setAttribute")
                .addArgument(new StringLiteralExpr(attribute.getKey()))
                .addArgument(new StringLiteralExpr(attribute.getValue()))));
  }

  private String generatedClassName(TypeElement type, String prefix, String suffix) {
    StringBuilder name = new StringBuilder(type.getSimpleName().toString());
    while (type.getEnclosingElement() instanceof TypeElement) {
      type = (TypeElement) type.getEnclosingElement();
      name.insert(0, type.getSimpleName() + "_");
    }
    String pkg = TypeSimplifier.packageNameOf(type);
    String dot = pkg.isEmpty() ? "" : ".";
    return pkg + dot + prefix + name + suffix;
  }

  // ------------------------------------------------------ @DataElement fields and methods
  private List<DataElementInfo> processDataElements(TypeElement type,
      TemplateSelector templateSelector, org.jsoup.nodes.Element root) {
    List<DataElementInfo> dataElements = new ArrayList<>();

    // fields
    Utils
        .getAnnotatedElements(iocContext.getGenerationContext().getElements(), type,
            DataField.class)
        .stream().filter(e -> e.getKind().isField()).map(e -> MoreElements.asVariable(e))
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
          dataElements.add(new DataElementInfo(typeName, field.getSimpleName().toString(), selector,
              kind, false));
        });

    // methods TODO
    ElementFilter.methodsIn(type.getEnclosedElements()).stream()
        .filter(method -> MoreElements.isAnnotationPresent(method, DataField.class))
        .forEach(method -> {

          // verify method
          if (method.getModifiers().contains(Modifier.PRIVATE)) {
            abortWithError(method,
                "@%s method must not be private " + method.getEnclosingElement() + ".",
                DataField.class.getSimpleName());
          }
          if (method.getModifiers().contains(Modifier.STATIC)) {
            abortWithError(method, "@%s method must not be static",
                DataField.class.getSimpleName());
          }
          DataElementInfo.Kind kind =
              templatedGeneratorUtils.getDataElementInfoKind(method.getReturnType());
          if (kind == DataElementInfo.Kind.Custom) {
            warning(method, "Unknown return type %s. Consider using one of %s.",
                method.getReceiverType(),
                EnumSet.complementOf(EnumSet.of(DataElementInfo.Kind.Custom)));
          }
          if (!method.getParameters().isEmpty()) {
            abortWithError(method, "@%s method must not have parameters",
                DataField.class.getSimpleName());
          }

          // verify the selector
          String selector = getSelector(method);
          verifySelector(selector, method, templateSelector, root);

          // verify the HTMLElement type
          String typeName =
              MoreTypes.asTypeElement(method.getReturnType()).getQualifiedName().toString();
          if (kind == DataElementInfo.Kind.HTMLElement) {
            verifyHTMLElement(typeName, selector, method, templateSelector, root);
          }
          // create info class for template processing
          dataElements.add(new DataElementInfo(typeName, method.getSimpleName().toString(),
              selector, kind, true));
        });

    return dataElements;
  }

  private void verifyHTMLElement(String htmlType, String selector, Element element,
      TemplateSelector templateSelector, org.jsoup.nodes.Element root) {
    // make sure the HTMLElement subtype maps to the right HTML tag
    Set<String> tags = HTML_ELEMENTS.get(htmlType);
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

  private String getSelector(Element element) {
    String selector = null;

    // noinspection Guava
    Optional<AnnotationMirror> annotationMirror =
        MoreElements.getAnnotationMirror(element, DataField.class);
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
        org.jboss.gwt.elemento.processor.TypeSimplifier.packageNameOf(type).replace('.', '/') + "/"
            + templateSelector.template;

    try {
      URL url = iocContext.getGenerationContext().getResourceOracle().findResource(fqTemplate);
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

  private void validateType(TypeElement type, Templated templated) {
    if (templated == null) {
      // This shouldn't happen unless the compilation environment is buggy,
      // but it has happened in the past and can crash the compiler.
      abortWithError(type,
          "Annotation processor for @%s was invoked with a type that does not have that "
              + "annotation; this is probably a compiler bug",
          Templated.class.getSimpleName());
    }
    if (type.getKind() != ElementKind.CLASS) {
      abortWithError(type, "@%s only applies to classes", Templated.class.getSimpleName());
    }
    if (ancestorIsTemplated(type)) {
      abortWithError(type, "One @%s class may not extend another", Templated.class.getSimpleName());
    }
    if (templatedGeneratorUtils.isAssignable(type, Annotation.class)) {
      abortWithError(type, "@%s may not be used to implement an annotation interface",
          Templated.class.getSimpleName());
    }
    if (!(templatedGeneratorUtils.isAssignable(type, IsElement.class)
        || templatedGeneratorUtils.isAssignable(type, io.crysknife.client.IsElement.class)
        || templatedGeneratorUtils.maybeGwtWidget(type.asType()))) {
      abortWithError(type, "%s must implement %s", type.getQualifiedName(),
          (IsElement.class.getCanonicalName() + " or "
              + io.crysknife.client.IsElement.class.getCanonicalName() + " or "
              + templatedGeneratorUtils.isWidgetType));
    }
  }

  private boolean ancestorIsTemplated(TypeElement type) {
    while (true) {
      TypeMirror parentMirror = type.getSuperclass();
      if (parentMirror.getKind() == TypeKind.NONE) {
        return false;
      }
      TypeElement parentElement =
          (TypeElement) processingEnvironment.getTypeUtils().asElement(parentMirror);
      if (parentElement.getAnnotation(Templated.class) != null) {
        return true;
      }
      type = parentElement;
    }
  }

  /**
   * Issue a compilation error and abandon the processing of this template. This does not prevent
   * the processing of other templates.
   */
  public void abortWithError(Element element, String msg, Object... args) {
    error(element, msg, args);
    throw new AbortProcessingException();
  }

  public void debug(String msg, Object... args) {
    if (this.processingEnvironment.getOptions().containsKey("debug")) {
      this.messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }
  }

  public void info(String msg, Object... args) {
    this.messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
  }

  public void warning(Element element, String msg, Object... args) {
    this.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, String.format(msg, args),
        element);
  }

  public void error(String msg, Object... args) {
    this.messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
  }

  public void error(Element element, String msg, Object... args) {
    StringBuffer sb = new StringBuffer();
    sb.append("Error at ").append(element.getEnclosingElement()).append(".")
        .append(element.getSimpleName()).append(" : ").append(String.format(msg, args));
    System.out.println(sb);
    this.messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
  }

  public void error(ProcessingException processingException) {
    if (processingException.getElement() != null) {
      this.messager.printMessage(Diagnostic.Kind.ERROR, processingException.getMessage(),
          processingException.getElement());
    } else {
      this.messager.printMessage(Diagnostic.Kind.ERROR, processingException.getMessage());
    }
  }

  private String getElementFromTag(TemplateContext context) {
    java.util.Optional<Map.Entry<String, Collection<String>>> result =
        HTML_ELEMENTS.asMap().entrySet().stream()
            .filter(e -> e.getValue().contains(context.getRoot().getTag())).findFirst();
    if (result.isPresent()) {
      return result.get().getKey();
    }
    return "elemental2.dom.HTMLElement";
  }

  private String escape(String unescaped) {
    int extra = 0;
    for (int in = 0, n = unescaped.length(); in < n; ++in) {
      switch (unescaped.charAt(in)) {
        case '\0':
        case '\n':
        case '\r':
        case '\"':
        case '\\':
          ++extra;
          break;
      }
    }

    if (extra == 0) {
      return unescaped;
    }

    char[] oldChars = unescaped.toCharArray();
    char[] newChars = new char[oldChars.length + extra];
    for (int in = 0, out = 0, n = oldChars.length; in < n; ++in, ++out) {
      char c = oldChars[in];
      switch (c) {
        case '\0':
          newChars[out++] = '\\';
          c = '0';
          break;
        case '\n':
          newChars[out++] = '\\';
          c = 'n';
          break;
        case '\r':
          newChars[out++] = '\\';
          c = 'r';
          break;
        case '\"':
          newChars[out++] = '\\';
          c = '"';
          break;
        case '\\':
          newChars[out++] = '\\';
          c = '\\';
          break;
      }
      newChars[out] = c;
    }

    return String.valueOf(newChars);
  }

  private static class RootNodeVisitor implements NodeVisitor {

    private org.jsoup.nodes.Element result;

    private String selector;

    private RootNodeVisitor(String selector) {
      this.selector = selector;
    }

    @Override
    public void head(org.jsoup.nodes.Node node, int i) {
      if (node.hasAttr("data-field")) {
        if (node.attr("data-field").equals(selector)) {
          result = (org.jsoup.nodes.Element) node;
        }
      } else if (node.hasAttr("id")) {
        if (node.attr("id").equals(selector)) {
          result = (org.jsoup.nodes.Element) node;
        }
      }
    }

    @Override
    public void tail(org.jsoup.nodes.Node node, int i) {

    }
  }

}
