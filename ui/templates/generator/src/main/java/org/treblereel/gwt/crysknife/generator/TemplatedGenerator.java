package org.treblereel.gwt.crysknife.generator;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
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
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
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
import jsinterop.base.Js;
import org.apache.commons.io.IOUtils;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.CanPlayThroughEvent;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ContextMenuEvent;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.DragEndEvent;
import org.gwtproject.event.dom.client.DragEnterEvent;
import org.gwtproject.event.dom.client.DragEvent;
import org.gwtproject.event.dom.client.DragLeaveEvent;
import org.gwtproject.event.dom.client.DragOverEvent;
import org.gwtproject.event.dom.client.DragStartEvent;
import org.gwtproject.event.dom.client.DropEvent;
import org.gwtproject.event.dom.client.EndedEvent;
import org.gwtproject.event.dom.client.ErrorEvent;
import org.gwtproject.event.dom.client.FocusEvent;
import org.gwtproject.event.dom.client.GestureChangeEvent;
import org.gwtproject.event.dom.client.GestureEndEvent;
import org.gwtproject.event.dom.client.GestureStartEvent;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.LoadEvent;
import org.gwtproject.event.dom.client.LoadedMetadataEvent;
import org.gwtproject.event.dom.client.LoseCaptureEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseOverEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseWheelEvent;
import org.gwtproject.event.dom.client.ProgressEvent;
import org.gwtproject.event.dom.client.ScrollEvent;
import org.gwtproject.event.dom.client.TouchCancelEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.CssResource;
import org.gwtproject.resources.context.AptContext;
import org.gwtproject.resources.ext.ResourceOracle;
import org.gwtproject.resources.rg.resource.impl.ResourceOracleImpl;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.UIObject;
import org.gwtproject.user.client.ui.Widget;
import org.jboss.elemento.IsElement;
import org.jboss.gwt.elemento.processor.AbortProcessingException;
import org.jboss.gwt.elemento.processor.ExpressionParser;
import org.jboss.gwt.elemento.processor.ProcessingException;
import org.jboss.gwt.elemento.processor.TemplateSelector;
import org.jboss.gwt.elemento.processor.TypeSimplifier;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.EventHandlerInfo;
import org.jboss.gwt.elemento.processor.context.RootElementInfo;
import org.jboss.gwt.elemento.processor.context.StyleSheet;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.jboss.gwt.elemento.template.TemplateUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.lesscss.LessCompiler;
import org.lesscss.LessException;
import org.lesscss.LessSource;
import org.treblereel.gwt.crysknife.annotation.DataField;
import org.treblereel.gwt.crysknife.annotation.EventHandler;
import org.treblereel.gwt.crysknife.annotation.ForEvent;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.annotation.Templated;
import org.treblereel.gwt.crysknife.client.Reflect;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.templates.client.StyleInjector;
import org.treblereel.gwt.crysknife.util.Utils;

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

    private static final HashBiMap<String, String> EVENTS = HashBiMap.create();

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

        EVENTS.put(BlurEvent.class.getCanonicalName(), "blur");
        EVENTS.put(CanPlayThroughEvent.class.getCanonicalName(), "canplaythrough");
        EVENTS.put(ChangeEvent.class.getCanonicalName(), "change");
        EVENTS.put(ClickEvent.class.getCanonicalName(), "click");
        EVENTS.put(ContextMenuEvent.class.getCanonicalName(), "contextmenu");
        EVENTS.put(DoubleClickEvent.class.getCanonicalName(), "dblclick");
        EVENTS.put(DragEndEvent.class.getCanonicalName(), "dragend");
        EVENTS.put(DragEnterEvent.class.getCanonicalName(), "dragenter");
        EVENTS.put(DragEvent.class.getCanonicalName(), "drag");
        EVENTS.put(DragLeaveEvent.class.getCanonicalName(), "dragleave");
        EVENTS.put(DragOverEvent.class.getCanonicalName(), "dragover");
        EVENTS.put(DragStartEvent.class.getCanonicalName(), "dragstart");
        EVENTS.put(DropEvent.class.getCanonicalName(), "drop");
        EVENTS.put(EndedEvent.class.getCanonicalName(), "ended");
        EVENTS.put(ErrorEvent.class.getCanonicalName(), "error");
        EVENTS.put(FocusEvent.class.getCanonicalName(), "focus");
        EVENTS.put(GestureChangeEvent.class.getCanonicalName(), "gesturechange");
        EVENTS.put(GestureEndEvent.class.getCanonicalName(), "gestureend");
        EVENTS.put(GestureStartEvent.class.getCanonicalName(), "gesturestart");
        EVENTS.put(KeyPressEvent.class.getCanonicalName(), "keypress");
        EVENTS.put(KeyDownEvent.class.getCanonicalName(), "keydown");
        EVENTS.put(KeyUpEvent.class.getCanonicalName(), "keyup");
        EVENTS.put(LoadedMetadataEvent.class.getCanonicalName(), "loadedmetadata");
        EVENTS.put(LoadEvent.class.getCanonicalName(), "load");
        EVENTS.put(LoseCaptureEvent.class.getCanonicalName(), "losecapture");
        EVENTS.put(MouseDownEvent.class.getCanonicalName(), "mousedown");
        EVENTS.put(MouseMoveEvent.class.getCanonicalName(), "mousemove");
        EVENTS.put(MouseOutEvent.class.getCanonicalName(), "mouseout");
        EVENTS.put(MouseOverEvent.class.getCanonicalName(), "mouseover");
        EVENTS.put(MouseUpEvent.class.getCanonicalName(), "mouseup");
        EVENTS.put(MouseWheelEvent.class.getCanonicalName(), "mousewheel");
        EVENTS.put(ProgressEvent.class.getCanonicalName(), "progress");
        EVENTS.put(ScrollEvent.class.getCanonicalName(), "scroll");
        EVENTS.put(TouchCancelEvent.class.getCanonicalName(), "touchcancel");
        EVENTS.put(TouchEndEvent.class.getCanonicalName(), "touchend");
        EVENTS.put(TouchMoveEvent.class.getCanonicalName(), "touchmove");
        EVENTS.put(TouchStartEvent.class.getCanonicalName(), "touchstart");
    }

    private ProcessingEnvironment processingEnvironment;
    private Messager messager;
    private BeanDefinition beanDefinition;
    private ResourceOracle oracle;

    public TemplatedGenerator(IOCContext iocContext) {
        super(iocContext);
    }

    @Override
    public void register() {
        this.processingEnvironment = iocContext.getGenerationContext().getProcessingEnvironment();
        this.messager = processingEnvironment.getMessager();

        iocContext.register(Templated.class, WiringElementType.CLASS_DECORATOR, this);

        oracle = new ResourceOracleImpl(new AptContext(iocContext.getGenerationContext().getProcessingEnvironment(),
                                                       iocContext.getGenerationContext().getRoundEnvironment()));
    }

    @Override
    public void generateBeanFactory(ClassBuilder builder, Definition definition) {
        if (definition instanceof BeanDefinition) {
            beanDefinition = (BeanDefinition) definition;
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

    private StyleSheet getStylesheet(TypeElement type, Templated templated) {
        if (Strings.emptyToNull(templated.stylesheet()) == null) {
            List<String> postfixes = Arrays.asList(".css", ".gss", ".less");
            String path = type.getQualifiedName().toString().replaceAll("\\.", "/");
            for (String postfix : postfixes) {
                URL file = oracle.findResource(path, postfix);
                try {
                    if (file != null && new File(file.toURI()).exists()) {
                        return new StyleSheet(type.getSimpleName() + "" + postfix, new File(file.toURI()));
                    }
                } catch (URISyntaxException e) {
                }
            }
        } else {
            try {
                return new StyleSheet(templated.stylesheet(), new File(oracle.findResource(templated.stylesheet()).toURI()));
            } catch (URISyntaxException e) {
            }
        }
        System.out.println(String.format("Unable to find stylesheet for %s", type.getQualifiedName()));

        return null;
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

        List<EventHandlerInfo> eventElements = processEventHandlers(type, context);
        context.setEvents(eventElements);

        // css/gss stylesheet
        context.setStylesheet(getStylesheet(type, templated));

        // generate code
        code(builder, context);
        info("Generated templated implementation [%s] for [%s]", context.getSubclass(), context.getBase());
    }

    private void code(ClassBuilder builder, TemplateContext templateContext) {
        addImports(builder);
        generateWrapper(builder, templateContext);
        setStylesheet(builder, templateContext);
        processDataFields(builder, templateContext);
        maybeInitWidgets(builder, templateContext);
        processEventHandlers(builder, templateContext);
    }

    private void maybeInitWidgets(ClassBuilder builder, TemplateContext templateContext) {
        List<DataElementInfo> widgets = templateContext.getDataElements()
                .stream()
                .filter(elm -> elm.getKind().equals(DataElementInfo.Kind.IsWidget))
                .collect(Collectors.toList());
        if (!widgets.isEmpty()) {
            builder.getClassCompilationUnit().addImport(List.class);
            builder.getClassCompilationUnit().addImport(ArrayList.class);
            builder.getClassCompilationUnit().addImport(Widget.class);

            ExpressionStmt expressionStmt = new ExpressionStmt();
            VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();

            VariableDeclarator variableDeclarator = new VariableDeclarator();
            variableDeclarator.setName("widgets");

            ClassOrInterfaceType list = new ClassOrInterfaceType().setName(List.class.getSimpleName());
            ClassOrInterfaceType arrayList = new ClassOrInterfaceType().setName(ArrayList.class.getSimpleName());
            list.setTypeArguments(new ClassOrInterfaceType().setName(Widget.class.getSimpleName()));

            variableDeclarator.setType(list);
            variableDeclarator.setInitializer(new ObjectCreationExpr().setType(arrayList));
            variableDeclarationExpr.getVariables().add(variableDeclarator);

            expressionStmt.setExpression(variableDeclarationExpr);

            builder.getGetMethodDeclaration()
                    .getBody()
                    .get()
                    .addAndGetStatement(expressionStmt);

            widgets.forEach(widget -> {
                FieldAccessExpr instance = new FieldAccessExpr(
                        new FieldAccessExpr(
                                new ThisExpr(), "instance"),
                        widget.getName());

                MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr("widgets"), "add")
                        .addArgument(instance);

                builder.getGetMethodDeclaration()
                        .getBody()
                        .get().addAndGetStatement(methodCallExpr);
            });

            MethodCallExpr doInit = new MethodCallExpr(new ClassOrInterfaceType()
                                                               .setName("TemplateUtil").getNameAsExpression(), "initTemplated")
                    .addArgument(Js.class.getCanonicalName() + ".uncheckedCast(this.instance.element())")
                    .addArgument("widgets");

            builder.getGetMethodDeclaration()
                    .getBody()
                    .get().addAndGetStatement(doInit);
        }
    }

    private void generateWrapper(ClassBuilder builder, TemplateContext templateContext) {
        ClassOrInterfaceDeclaration wrapper = new ClassOrInterfaceDeclaration();
        wrapper.setName(beanDefinition.getClassName());
        wrapper.addExtendedType(beanDefinition.getQualifiedName());
        wrapper.setModifier(com.github.javaparser.ast.Modifier.Keyword.FINAL, true);
        String element = getElementFromTag(templateContext);

        wrapper.addFieldWithInitializer(element,
                                        "root",
                                        new CastExpr(new ClassOrInterfaceType().setName(element), new MethodCallExpr(new FieldAccessExpr(
                                                new NameExpr("elemental2.dom.DomGlobal"), "document"),
                                                                                                                     "createElement").addArgument(
                                                new StringLiteralExpr(templateContext.getRoot().getTag()))),
                                        com.github.javaparser.ast.Modifier.Keyword.PRIVATE,
                                        com.github.javaparser.ast.Modifier.Keyword.FINAL);

        ConstructorDeclaration constructor = wrapper.addConstructor(com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
        if (beanDefinition.getConstructorInjectionPoint() != null) {
            List<String> args = new LinkedList<>();
            for (FieldPoint argument : beanDefinition.getConstructorInjectionPoint().getArguments()) {
                constructor.addAndGetParameter(argument.getType().getQualifiedName().toString(), argument.getName());
                args.add(argument.getName());
            }
            StringJoiner joiner = new StringJoiner(",");
            args.stream().forEach(joiner::add);
            constructor.getBody().addStatement("super(" + joiner.toString() + ");");
        }
        setAttributes(constructor.getBody(), templateContext);
        setInnerHTML(constructor.getBody(), templateContext);

        addElementMethod(wrapper, templateContext);

        builder.getClassDeclaration().addMember(wrapper);
    }

    private void addElementMethod(ClassOrInterfaceDeclaration wrapper, TemplateContext templateContext) {
        String element = getElementFromTag(templateContext);

        MethodDeclaration method = wrapper.addMethod("element",
                                                     com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
        method.addAnnotation(Override.class);
        method.setType(element);
        ReturnStmt _return = new ReturnStmt(new CastExpr(new ClassOrInterfaceType().setName(element),
                                                         new FieldAccessExpr(new ThisExpr(), "root")));

        method.getBody().get().addStatement(_return);
    }

    private void setStylesheet(ClassBuilder builder, TemplateContext templateContext) {
        if (templateContext.getStylesheet() != null) {
            builder.getClassCompilationUnit().addImport(StyleInjector.class);

            if (!templateContext.getStylesheet().isLess()) {
                builder.getClassCompilationUnit().addImport(CssResource.class);
                builder.getClassCompilationUnit().addImport(CssResource.NotStrict.class);
                builder.getClassCompilationUnit().addImport(Resource.class);
                builder.getClassCompilationUnit().addImport(ClientBundle.Source.class);

                ClassOrInterfaceDeclaration inner = new ClassOrInterfaceDeclaration();
                inner.setName("Stylesheet");
                inner.setInterface(true);
                inner.addAnnotation("Resource");

                new JavaParser().parseBodyDeclaration("@Source(\"" + templateContext.getStylesheet().getStyle() + "\") @NotStrict CssResource getStyle();").ifSuccessful(inner::addMember);
                builder.getClassDeclaration().addMember(inner);

                String theName = Utils.getFactoryClassName(beanDefinition.getType()) + "_StylesheetImpl";

                //TODO Temporary workaround, till gwt-dom StyleInjector ll be fixed
                builder.getGetMethodDeclaration()
                        .getBody()
                        .get().addStatement(new MethodCallExpr(new MethodCallExpr(new ClassOrInterfaceType()
                                                                                          .setName("StyleInjector")
                                                                                          .getNameAsExpression(), "fromString").addArgument(new MethodCallExpr(
                        new MethodCallExpr(
                                new ObjectCreationExpr()
                                        .setType(theName),
                                "getStyle"),
                        "getText")), "inject"));
            } else {
                try {
                    final LessSource source = new LessSource(templateContext.getStylesheet().getFile());
                    final LessCompiler compiler = new LessCompiler();
                    final String compiledCss = compiler.compile(source);
                    builder.getGetMethodDeclaration()
                            .getBody()
                            .get().addStatement(new MethodCallExpr(
                            new MethodCallExpr(
                                    new ClassOrInterfaceType()
                                            .setName("StyleInjector")
                                            .getNameAsExpression(), "fromString").addArgument(new StringLiteralExpr(org.gwtproject.resources.rg.Generator.escape(compiledCss))), "inject"));
                } catch (LessException | IOException e) {
                    throw new Error("Unable to process Less " + templateContext.getStylesheet());
                }
            }
        }
    }

    private void processDataFields(ClassBuilder builder, TemplateContext templateContext) {
        templateContext.getDataElements().forEach(element -> {
            MethodCallExpr resolveElement;
            MethodCallExpr fieldAccessCallExpr = getFieldAccessCallExpr(element.getName());

            IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(fieldAccessCallExpr, new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));
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

            MethodCallExpr fieldSetCallExpr = new MethodCallExpr(
                    new MethodCallExpr(
                            new NameExpr(Js.class.getSimpleName()), "asPropertyMap")
                            .addArgument("instance"), "set")
                    .addArgument(new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                                         .addArgument(new StringLiteralExpr(
                                                 Utils.getJsFieldName(getVariableElement(element.getName()))))
                                         .addArgument("instance"))
                    .addArgument(resolveElement);

            ifStmt.setThenStmt(new BlockStmt().addAndGetStatement(fieldSetCallExpr));
            ifStmt.setElseStmt(new BlockStmt().
                    addAndGetStatement(new MethodCallExpr(new ClassOrInterfaceType()
                                                                  .setName("TemplateUtil").getNameAsExpression(), "replaceElement")
                                               .addArgument("this.instance.element()")
                                               .addArgument(new StringLiteralExpr(element.getSelector()))
                                               .addArgument(getInstanceByElementKind(element, fieldAccessCallExpr))));
            builder.getGetMethodDeclaration()
                    .getBody()
                    .get().addAndGetStatement(ifStmt);
        });
    }

    private MethodCallExpr getFieldAccessCallExpr(String fieldName) {
        VariableElement field = getVariableElement(fieldName);
        return getFieldAccessCallExpr(field);
    }

    private MethodCallExpr getFieldAccessCallExpr(VariableElement field) {
        return new MethodCallExpr(
                new MethodCallExpr(
                        new NameExpr(Js.class.getSimpleName()), "asPropertyMap")
                        .addArgument("instance"), "get")
                .addArgument(new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                                     .addArgument(new StringLiteralExpr(Utils.getJsFieldName(field)))
                                     .addArgument("instance"));
    }

    private Expression getInstanceByElementKind(DataElementInfo element, Expression instance) {
        if (element.getKind().equals(DataElementInfo.Kind.IsElement)) {
            instance = new MethodCallExpr(
                    new EnclosedExpr(
                            new CastExpr(
                                    new ClassOrInterfaceType()
                                            .setName(IsElement.class.getCanonicalName()), instance)),
                    "element");
        } else if (element.getKind()
                .equals(DataElementInfo.Kind.IsWidget)) {

            return new MethodCallExpr(new NameExpr(Js.class.getCanonicalName()),
                                      "<" + HTMLElement.class.getCanonicalName() + ">uncheckedCast")
                    .addArgument(new MethodCallExpr(
                            new EnclosedExpr(
                                    new CastExpr(
                                            new ClassOrInterfaceType().setName(UIObject.class.getCanonicalName()), instance)), "getElement"));
        }

        return new EnclosedExpr(new CastExpr(new ClassOrInterfaceType().setName(HTMLElement.class.getCanonicalName()), instance));
    }

    private VariableElement getVariableElement(String elementName) {
        return beanDefinition
                .getType()
                .getEnclosedElements()
                .stream()
                .filter(elm -> elm.getKind().equals(ElementKind.FIELD))
                .filter(elm -> elm.getSimpleName().toString().equals(elementName))
                .map(elm -> MoreElements.asVariable(elm))
                .findFirst().orElseThrow(() -> new Error("Unable to find @DataField " + elementName + " in " + beanDefinition.getClassName()));
    }

    private void processEventHandlers(ClassBuilder builder, TemplateContext templateContext) {
        templateContext.getEvents().forEach(event -> {
            for (String eventEvent : event.getEvents()) {
                MethodCallExpr fieldAccessCallExpr = getFieldAccessCallExpr(event.getInfo().getName());

                MethodCallExpr methodCallExpr = new MethodCallExpr(getInstanceByElementKind(event.getInfo(), fieldAccessCallExpr), "addEventListener")
                        .addArgument(new StringLiteralExpr(eventEvent))
                        .addArgument(new NameExpr("e -> this.instance." + event.getMethodName() + "(jsinterop.base.Js.uncheckedCast(e))"));

                builder.getGetMethodDeclaration()
                        .getBody()
                        .get().addAndGetStatement(methodCallExpr);
            }
        });
    }

    private void addImports(ClassBuilder builder) {
        builder.getClassCompilationUnit().addImport(DomGlobal.class);
        builder.getClassCompilationUnit().addImport(TemplateUtil.class);
        builder.getClassCompilationUnit().addImport(Js.class);
        builder.getClassCompilationUnit().addImport(Reflect.class);
    }

    private void setInnerHTML(BlockStmt block, TemplateContext templateContext) {
        block.addAndGetStatement(
                new AssignExpr().setTarget(new FieldAccessExpr(
                        new FieldAccessExpr(
                                new ThisExpr(), "root"), "innerHTML"))
                        .setValue(new StringLiteralExpr(templateContext.getRoot().getInnerHtml())));
    }

    private void setAttributes(BlockStmt block, TemplateContext templateContext) {
        templateContext.getRoot().getAttributes().forEach(attribute -> block.addAndGetStatement(new MethodCallExpr(
                new FieldAccessExpr(
                        new ThisExpr(), "root"), "setAttribute")
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
    private List<DataElementInfo> processDataElements(TypeElement type, TemplateSelector templateSelector,
                                                      org.jsoup.nodes.Element root) {
        List<DataElementInfo> dataElements = new ArrayList<>();

        // fields
        ElementFilter.fieldsIn(type.getEnclosedElements()).stream()
                .filter(field -> MoreElements.isAnnotationPresent(field, DataField.class))
                .forEach(field -> {

                    // verify the field
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

    private List<EventHandlerInfo> processEventHandlers(TypeElement type, TemplateContext templateContext) {
        List<EventHandlerInfo> eventHandlerElements = new ArrayList<>();

        Element forEvent = iocContext.getGenerationContext().getElements().getTypeElement(ForEvent.class.getCanonicalName());
        Element domEvent = iocContext.getGenerationContext().getElements().getTypeElement(DomEvent.class.getCanonicalName());

        // fields
        ElementFilter.methodsIn(type.getEnclosedElements()).stream()
                .filter(method -> MoreElements.isAnnotationPresent(method, EventHandler.class))
                .forEach(method -> {

                    // verify the field
                    if (method.getModifiers().contains(Modifier.PRIVATE)) {
                        abortWithError(method, "@%s method must not be private", EventHandler.class.getSimpleName());
                    }
                    if (method.getModifiers().contains(Modifier.STATIC)) {
                        abortWithError(method, "@%s method must not be static", EventHandler.class.getSimpleName());
                    }

                    if (method.getParameters().isEmpty() || method.getParameters().size() > 1) {
                        abortWithError(method, "@%s method must have one parameter",
                                       EventHandler.class.getSimpleName());
                    }

                    VariableElement parameter = method.getParameters().get(0);
                    DeclaredType declaredType = MoreTypes.asDeclared(parameter.asType());

                    if (parameter.getAnnotation(ForEvent.class) == null && !EVENTS.containsKey(declaredType.toString())) {
                        abortWithError(method, "@%s's method must have one parameter and this parameter must be annotated with @%s or be subtype of %s,", method.getEnclosingElement(), forEvent, domEvent);
                    }

                    if ((parameter.getAnnotation(ForEvent.class) != null &&
                            (parameter.getAnnotation(ForEvent.class).value() == null ||
                                    parameter.getAnnotation(ForEvent.class).value().length == 0))
                            && !EVENTS.containsKey(declaredType.toString())) {
                        abortWithError(method, "@%s value must not be empty ",
                                       ForEvent.class.getSimpleName());
                    }

                    String[] events = getEvents(parameter);

                    String[] dataElements = method.getAnnotation(EventHandler.class).value();

                    TypeElement event = iocContext.getGenerationContext().getElements().getTypeElement(Event.class.getCanonicalName());

                    if (!iocContext.getGenerationContext().getTypes().isSubtype(declaredType, event.asType()) &&
                            !EVENTS.containsKey(declaredType.toString())) {
                        abortWithError(method.getEnclosingElement(), "@%s method must have only one parameter and this parameter must be type or subtype of  "
                                + event.getQualifiedName() + " or " + domEvent + ", ", EventHandler.class.getSimpleName());
                    }

                    Arrays.stream(dataElements).forEach(data -> {
                        java.util.Optional<DataElementInfo> result = templateContext.getDataElements().stream().filter(elm -> elm.getSelector().equals(data)).findFirst();
                        if (result.isPresent()) {
                            DataElementInfo info = result.get();
                            eventHandlerElements.add(new EventHandlerInfo(info, events, method.getSimpleName().toString(), declaredType.toString()));
                        } else {
                            abortWithError(method, "Unable to find DataField element with name or alias " + data + " from ");
                        }
                    });
                });

        return eventHandlerElements;
    }

    private String[] getEvents(VariableElement parameter) {
        if (parameter.getAnnotation(ForEvent.class) != null) {
            return parameter.getAnnotation(ForEvent.class).value();
        }
        String[] result = new String[1];
        result[0] = EVENTS.get(MoreTypes.asDeclared(parameter.asType()).toString());
        return result;
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
        } else if (isAssignable(dataElementType, IsWidget.class)) {
            return DataElementInfo.Kind.IsWidget;
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
        String typeParam = HTMLElement.class.getCanonicalName();
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

        try {
            URL url = oracle.findResource(fqTemplate);
            if (url == null) {
                abortWithError(type, "Cannot find template \"%s\". Please make sure the template exists.", fqTemplate);
            }
            Document document = Jsoup.parse(IOUtils.toString(oracle.findResource(fqTemplate), Charset.defaultCharset()));
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

    private String getElementFromTag(TemplateContext context) {
        java.util.Optional<Map.Entry<String, Collection<String>>> result = HTML_ELEMENTS.asMap()
                .entrySet()
                .stream()
                .filter(e -> e.getValue()
                        .contains(context.getRoot().getTag())).findFirst();
        if (result.isPresent()) {
            return result.get().getKey();
        }
        return "elemental2.dom.HTMLElement";
    }
}