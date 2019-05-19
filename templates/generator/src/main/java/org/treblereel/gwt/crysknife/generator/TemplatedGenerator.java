package org.treblereel.gwt.crysknife.generator;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
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
import org.jboss.gwt.elemento.core.IsElement;
import org.jboss.gwt.elemento.processor.AbortProcessingException;
import org.jboss.gwt.elemento.processor.ExpressionParser;
import org.jboss.gwt.elemento.processor.ProcessingException;
import org.jboss.gwt.elemento.processor.TemplateSelector;
import org.jboss.gwt.elemento.processor.TypeSimplifier;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.RootElementInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.jboss.gwt.elemento.template.TemplateUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.annotation.Templated;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/7/19
 */
@Generator
public class TemplatedGenerator extends IOCGenerator {

    private static final String QUOTE = "\"";
    private static final Escaper JAVA_STRING_ESCAPER = Escapers.builder()
            .addEscape('"', "\\\"")
            .addEscape('\n', "")
            .addEscape('\r', "")
            .build();
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
        HTML_ELEMENTS.putAll(HTMLHeadingElement.class.getName(), asList("h1", "h2", "h3", "h4", "h5", "h6"));
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
        HTML_ELEMENTS.put(HTMLTextAreaElement.class.getName(), "textarea");
        HTML_ELEMENTS.put(HTMLTableRowElement.class.getName(), "tr");
        HTML_ELEMENTS.put(HTMLTrackElement.class.getName(), "track");
        HTML_ELEMENTS.put(HTMLUListElement.class.getName(), "ul");
        HTML_ELEMENTS.put(HTMLVideoElement.class.getName(), "video");
    }

    private IOCContext iocContext;
    private Filer filer;
    private RoundEnvironment roundEnvironment;
    private ProcessingEnvironment processingEnvironment;
    private Messager messager;

    @Override
    public void register(IOCContext iocContext) {
        this.iocContext = iocContext;
        this.roundEnvironment = iocContext.getGenerationContext().getRoundEnvironment();
        this.processingEnvironment = iocContext.getGenerationContext().getProcessingEnvironment();
        this.filer = processingEnvironment.getFiler();
        this.messager = processingEnvironment.getMessager();

        iocContext.register(Templated.class, WiringElementType.CLASS_DECORATOR, this);
    }

    @Override
    public void generateBeanFactory(ClassBuilder builder, Definition definition) {
        if (definition instanceof BeanDefinition) {
            BeanDefinition beanDefinition = (BeanDefinition) definition;
            validateType(beanDefinition.getType(), beanDefinition.getType().getAnnotation(Templated.class));
            processType(builder, beanDefinition.getType(), beanDefinition.getType().getAnnotation(Templated.class));
        }
    }

    // ------------------------------------------------------ root element / template
    private RootElementInfo createRootElementInfo(org.jsoup.nodes.Element root, String subclass) {
        List<Attribute> attributes = root.attributes().asList().stream()
                .filter(attribute -> !attribute.getKey().equals("data-field"))
                .collect(Collectors.toList());

        ExpressionParser expressionParser = new ExpressionParser();
        String html = root.children().isEmpty() ? null : JAVA_STRING_ESCAPER.escape(root.html());
        Map<String, String> expressions = expressionParser.parse(html);
        expressions.putAll(expressionParser.parse(root.outerHtml()));

        return new RootElementInfo(root.tagName(), subclass.toLowerCase() + "_root_element",
                                   attributes, html, expressions);
    }

    private TemplateSelector getTemplateSelector(TypeElement type, Templated templated) {
        if (Strings.emptyToNull(templated.value()) == null) {
            return new TemplateSelector(type.getSimpleName().toString() + ".html");
        } else {
            if (templated.value().contains("#")) {
                Iterator<String> iterator = Splitter.on('#')
                        .limit(2)
                        .omitEmptyStrings()
                        .trimResults()
                        .split(templated.value())
                        .iterator();
                return new TemplateSelector(iterator.next(), iterator.next());
            } else {
                return new TemplateSelector(templated.value());
            }
        }
    }

    private void processType(ClassBuilder builder, TypeElement type, Templated templated) {
        String isElementTypeParameter = getIsElementTypeParameter(type.getInterfaces());
        String subclass = TypeSimplifier.simpleNameOf(generatedClassName(type, "Templated_", ""));
        TemplateContext context = new TemplateContext(TypeSimplifier.packageNameOf(type),
                                                      TypeSimplifier.classNameOf(type), subclass, isElementTypeParameter, "qwertyuiop");
        // root element and template
        TemplateSelector templateSelector = getTemplateSelector(type, templated);
        org.jsoup.nodes.Element root = parseTemplate(type, templateSelector);
        context.setRoot(createRootElementInfo(root, subclass));

        // find and verify all @DataField members
        List<DataElementInfo> dataElements = processDataElements(type, templateSelector, root);
        context.setDataElements(dataElements);

        // generate code
        code(builder, context);
        info("Generated templated implementation [%s] for [%s]", context.getSubclass(), context.getBase());
    }

    private void code(ClassBuilder builder, TemplateContext templateContext) {
        addImports(builder);
        setAttributes(builder, templateContext);
        setInnerHTML(builder, templateContext);
        processDataFields(builder, templateContext);
    }

    private void processDataFields(ClassBuilder builder, TemplateContext templateContext) {
        templateContext.getDataElements().forEach(element -> {
            FieldAccessExpr instance = new FieldAccessExpr(new FieldAccessExpr(new ThisExpr(), "instance"), element.getName());
            MethodCallExpr resolveElement;
            IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(instance, new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));
            if (element.needsCast()) {
                resolveElement = new MethodCallExpr(new ClassOrInterfaceType()
                                                            .setName("TemplateUtil")
                                                            .getNameAsExpression(), "resolveElementAs")
                        .setTypeArguments(new ClassOrInterfaceType().setName(element.getType()))
                        .addArgument("this.instance.element()")
                        .addArgument(new StringLiteralExpr(element.getSelector()));
            } else {
                resolveElement = new MethodCallExpr(new ClassOrInterfaceType()
                                                            .setName("TemplateUtil")
                                                            .getNameAsExpression(), "resolveElement")
                        .addArgument("this.instance.element()")
                        .addArgument(new StringLiteralExpr(element.getName()));
            }

            ifStmt.setThenStmt(new BlockStmt().addAndGetStatement(new AssignExpr().setTarget(instance).setValue(resolveElement)));

            ifStmt.setElseStmt(new BlockStmt().addAndGetStatement(new MethodCallExpr(new ClassOrInterfaceType()
                                                                                             .setName("TemplateUtil").getNameAsExpression(), "replaceElement")
                                                                          .addArgument("this.instance.element()")
                                                                          .addArgument(new StringLiteralExpr(element.getSelector()))
                                                                          .addArgument("this.instance." + element.getName())));

            builder.getGetMethodDeclaration()
                    .getBody()
                    .get().addAndGetStatement(ifStmt);
        });
    }

    private void addImports(ClassBuilder builder) {
        builder.getClassCompilationUnit().addImport(DomGlobal.class);
        builder.getClassCompilationUnit().addImport(TemplateUtil.class);
    }

    private void setInnerHTML(ClassBuilder builder, TemplateContext templateContext) {
        builder.getGetMethodDeclaration()
                .getBody()
                .get()
                .addAndGetStatement(
                        new AssignExpr().setTarget(new FieldAccessExpr(
                                new MethodCallExpr(
                                        new FieldAccessExpr(
                                                new ThisExpr(), "instance"), "element"), "innerHTML"))
                                .setValue(new StringLiteralExpr(templateContext.getRoot().getInnerHtml())));
    }

    private void setAttributes(ClassBuilder builder, TemplateContext templateContext) {
        templateContext.getRoot().getAttributes().forEach(attribute -> {
            builder.getGetMethodDeclaration()
                    .getBody()
                    .get()
                    .addAndGetStatement(new MethodCallExpr(new MethodCallExpr(
                            new FieldAccessExpr(
                                    new ThisExpr(), "instance"), "element"), "setAttribute")
                                                .addArgument(new StringLiteralExpr(attribute.getKey()))
                                                .addArgument(new StringLiteralExpr(attribute.getValue())));
        });
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

    private List<DataElementInfo> processDataElements(TypeElement type, TemplateSelector templateSelector,
                                                      org.jsoup.nodes.Element root) {
        List<DataElementInfo> dataElements = new ArrayList<>();

        // fields
        ElementFilter.fieldsIn(type.getEnclosedElements()).stream()
                .filter(field -> MoreElements.isAnnotationPresent(field, DataField.class))
                .forEach(field -> {

                    // verify the field
                    if (field.getModifiers().contains(Modifier.PRIVATE)) {
                        abortWithError(field, "@%s member must not be private", DataField.class.getSimpleName());
                    }
                    if (field.getModifiers().contains(Modifier.STATIC)) {
                        abortWithError(field, "@%s member must not be static", DataField.class.getSimpleName());
                    }
                    DataElementInfo.Kind kind = getDataElementInfoKind(field.asType());
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

        // methods
        ElementFilter.methodsIn(type.getEnclosedElements()).stream()
                .filter(method -> MoreElements.isAnnotationPresent(method, DataField.class))
                .forEach(method -> {

                    // verify method
                    if (method.getModifiers().contains(Modifier.PRIVATE)) {
                        abortWithError(method, "@%s method must not be private", DataField.class.getSimpleName());
                    }
                    if (method.getModifiers().contains(Modifier.STATIC)) {
                        abortWithError(method, "@%s method must not be static", DataField.class.getSimpleName());
                    }
                    DataElementInfo.Kind kind = getDataElementInfoKind(method.getReturnType());
                    if (kind == DataElementInfo.Kind.Custom) {
                        warning(method, "Unknown return type %s. Consider using one of %s.", method.getReceiverType(),
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
                    String typeName = MoreTypes.asTypeElement(method.getReturnType())
                            .getQualifiedName().toString();
                    if (kind == DataElementInfo.Kind.HTMLElement) {
                        verifyHTMLElement(typeName, selector, method, templateSelector, root);
                    }
                    // create info class for template processing
                    dataElements.add(new DataElementInfo(typeName, method.getSimpleName().toString(), selector,
                                                         kind, true));
                });

        return dataElements;
    }

    private void verifyHTMLElement(String htmlType, String selector, Element element, TemplateSelector templateSelector,
                                   org.jsoup.nodes.Element root) {
        // make sure the HTMLElement subtype maps to the right HTML tag
        Set<String> tags = HTML_ELEMENTS.get(htmlType);
        if (!tags.isEmpty()) {
            Elements elements = root.getElementsByAttributeValue("data-field", selector);
            if (!elements.isEmpty()) {
                String tagName = elements.get(0).tagName().toLowerCase();
                if (!tags.contains(tagName)) {
                    String fieldOrMethod = element instanceof VariableElement ? "field" : "method";
                    String expected = tags.size() == 1
                            ? QUOTE + tags.iterator().next() + QUOTE
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
        Elements elements = root.getElementsByAttributeValue("data-field", selector);
        if (elements.isEmpty()) {
            abortWithError(element,
                           "Cannot find a matching element in %s using \"[data-field=%s]\" as selector",
                           templateSelector, selector);
        } else if (elements.size() > 1) {
            warning(element,
                    "Found %d matching elements in %s using \"[data-field=%s]\" as selector. Only the first will be used.",
                    elements.size(), templateSelector, selector);
        }
    }

    private DataElementInfo.Kind getDataElementInfoKind(TypeMirror dataElementType) {
        if (isAssignable(dataElementType, HTMLElement.class)) {
            return DataElementInfo.Kind.HTMLElement;
        } else if (isAssignable(dataElementType, IsElement.class)) {
            return DataElementInfo.Kind.IsElement;
        } else {
            return DataElementInfo.Kind.Custom;
        }
    }

    private String getSelector(Element element) {
        String selector = null;

        //noinspection Guava
        Optional<AnnotationMirror> annotationMirror = MoreElements
                .getAnnotationMirror(element, DataField.class);
        if (annotationMirror.isPresent()) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> values = processingEnvironment.getElementUtils()
                    .getElementValuesWithDefaults(annotationMirror.get());
            if (!values.isEmpty()) {
                selector = String.valueOf(values.values().iterator().next().getValue());
            }
        }
        return Strings.emptyToNull(selector) == null ? element.getSimpleName().toString() : selector;
    }

    private String getIsElementTypeParameter(List<? extends TypeMirror> interfaces) {
        String typeParam = "elemental2.dom.HTMLElement";
        for (TypeMirror interfaceMirror : interfaces) {
            if (MoreTypes.isTypeOf(IsElement.class, interfaceMirror)) {
                DeclaredType interfaceDeclaration = MoreTypes.asDeclared(interfaceMirror);
                List<? extends TypeMirror> typeArguments = interfaceDeclaration.getTypeArguments();
                if (!typeArguments.isEmpty()) {
                    TypeElement typeArgument = (TypeElement) processingEnvironment
                            .getTypeUtils()
                            .asElement(typeArguments.get(0));
                    return typeArgument.getQualifiedName().toString();
                }
            }
        }
        return typeParam;
    }

    private org.jsoup.nodes.Element parseTemplate(TypeElement type, TemplateSelector templateSelector) {
        org.jsoup.nodes.Element root = null;
        String fqTemplate = org.jboss.gwt.elemento.processor.TypeSimplifier.packageNameOf(type).replace('.', '/') + "/" + templateSelector.template;

        JavaFileManager.Location[] locations = new JavaFileManager.Location[]{
                StandardLocation.SOURCE_PATH,
                StandardLocation.CLASS_PATH,
                StandardLocation.SOURCE_OUTPUT,
                StandardLocation.CLASS_OUTPUT,
        };

        FileObject templateResource = null;
        for (JavaFileManager.Location location : locations) {
            try {

                FileObject temp = filer.getResource(location, "", fqTemplate);
                if (new File(temp.getName()).exists()) {
                    templateResource = temp;
                }
            } catch (IOException e) {
            }
        }
        if (templateResource == null) {
            abortWithError(type, "Cannot find template \"%s\". Please make sure the template exists.", fqTemplate);
        }
        try {
            Document document = Jsoup.parse(templateResource.getCharContent(true).toString());
            if (templateSelector.hasSelector()) {
                String query = "[data-field=" + templateSelector.selector + "]";
                Elements selector = document.select(query);
                if (selector.isEmpty()) {
                    abortWithError(type, "Unable to select HTML from \"%s\" using \"%s\"", templateSelector.template,
                                   query);
                } else {
                    root = selector.first();
                }
            } else {
                if (document.body() == null || document.body().children().isEmpty()) {
                    abortWithError(type, "No content found in the <body> of \"%s\"", templateSelector.template);
                } else {
                    root = document.body().children().first();
                }
            }
        } catch (IOException e) {
            abortWithError(type, "Unable to read template \"%s\": %s", fqTemplate, e.getMessage());
        }
        return root;
    }

    private FileObject findTemplate(String name) {
        FileObject resource = null;
        JavaFileManager.Location[] locations = new JavaFileManager.Location[]{
                StandardLocation.SOURCE_PATH,
                StandardLocation.CLASS_PATH,
                StandardLocation.SOURCE_OUTPUT,
                StandardLocation.CLASS_OUTPUT,
        };
        for (JavaFileManager.Location location : locations) {
            try {
                resource = filer.getResource(location, "", name);
                if (resource != null) {
                    return resource;
                }
            } catch (IOException ignored) {
                System.out.println(String.format("Unable to find %s in %s: %s", name, location.getName(), ignored.getMessage()));
            }
        }
        return null;
    }

    private boolean isAssignable(TypeElement subType, Class<?> baseType) {
        return isAssignable(subType.asType(), baseType);
    }

    private boolean isAssignable(TypeMirror subType, Class<?> baseType) {
        return isAssignable(subType, getTypeMirror(baseType));
    }

    private boolean isAssignable(TypeMirror subType, TypeMirror baseType) {
        return processingEnvironment.getTypeUtils().isAssignable(processingEnvironment
                                                                         .getTypeUtils()
                                                                         .erasure(subType),
                                                                 processingEnvironment
                                                                         .getTypeUtils()
                                                                         .erasure(baseType));
    }

    private TypeMirror getTypeMirror(Class<?> c) {
        return processingEnvironment.getElementUtils().getTypeElement(c.getName()).asType();
    }

    private void validateType(TypeElement type, Templated templated) {
        if (templated == null) {
            // This shouldn't happen unless the compilation environment is buggy,
            // but it has happened in the past and can crash the compiler.
            abortWithError(type, "Annotation processor for @%s was invoked with a type that does not have that " +
                    "annotation; this is probably a compiler bug", Templated.class.getSimpleName());
        }
        if (type.getKind() != ElementKind.CLASS) {
            abortWithError(type, "@%s only applies to classes", Templated.class.getSimpleName());
        }
        if (ancestorIsTemplated(type)) {
            abortWithError(type, "One @%s class may not extend another", Templated.class.getSimpleName());
        }
        if (isAssignable(type, Annotation.class)) {
            abortWithError(type, "@%s may not be used to implement an annotation interface",
                           Templated.class.getSimpleName());
        }
        if (!isAssignable(type, IsElement.class)) {
            abortWithError(type, "%s must implement %s", type.getSimpleName(), IsElement.class.getSimpleName());
        }
    }

    private boolean ancestorIsTemplated(TypeElement type) {
        while (true) {
            TypeMirror parentMirror = type.getSuperclass();
            if (parentMirror.getKind() == TypeKind.NONE) {
                return false;
            }
            TypeElement parentElement = (TypeElement) processingEnvironment.getTypeUtils().asElement(parentMirror);
            if (parentElement.getAnnotation(Templated.class) != null) {
                return true;
            }
            type = parentElement;
        }
    }

    /**
     * Issue a compilation error and abandon the processing of this template. This does not prevent the
     * processing of other templates.
     */
    private void abortWithError(Element element, String msg, Object... args) throws AbortProcessingException {
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
        this.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, String.format(msg, args), element);
    }

    public void error(String msg, Object... args) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    public void error(Element element, String msg, Object... args) {
        System.out.println("Error " + String.format(msg, args) + " " + element.toString());
        this.messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
    }

    public void error(ProcessingException processingException) {
        if (processingException.getElement() != null) {
            this.messager.printMessage(Diagnostic.Kind.ERROR, processingException.getMessage(), processingException.getElement());
        } else {
            this.messager.printMessage(Diagnostic.Kind.ERROR, processingException.getMessage());
        }
    }
}