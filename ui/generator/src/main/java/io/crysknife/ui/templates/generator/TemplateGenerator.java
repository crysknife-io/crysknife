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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Supplier;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
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
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.templates.client.EventHandlerHolder;
import io.crysknife.ui.templates.client.EventHandlerRegistration;
import io.crysknife.ui.templates.client.TemplateUtil;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.templates.generator.dto.TemplateDefinition;
import io.crysknife.ui.templates.generator.events.EventHandlerTemplatedProcessor;
import io.crysknife.ui.translation.client.annotation.TranslationBundle;
import jsinterop.base.Js;
import org.jboss.gwt.elemento.processor.TemplateSelector;
import org.jboss.gwt.elemento.processor.TypeSimplifier;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.EventHandlerInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.treblereel.j2cl.processors.utils.J2CLUtils;

import static com.google.auto.common.MoreTypes.asElement;

@Generator
public class TemplateGenerator extends IOCGenerator<BeanDefinition> {

    private final TypeElement isElement;
    private final J2CLUtils j2CLUtils;
    private final TemplateValidator templateValidator;
    private final TemplateParser templateParser;
    private final DataFieldGenerator dataFieldGenerator;
    private final DataFieldProcessor dataFieldProcessor;
    private final StylesheetProcessor stylesheetProcessor;
    private final EventHandlerTemplatedProcessor eventHandlerTemplatedProcessor;

    private final FreemarkerTemplateGenerator freemarkerTemplateGenerator =
            new FreemarkerTemplateGenerator("ui.ftlh");

    public TemplateGenerator(TreeLogger treeLogger, IOCContext iocContext) {
        super(treeLogger, iocContext);
        this.j2CLUtils = new J2CLUtils(iocContext.getGenerationContext().getProcessingEnvironment());
        TemplatedGeneratorUtils templatedGeneratorUtils = new TemplatedGeneratorUtils(iocContext);

        this.templateValidator = new TemplateValidator(iocContext);
        this.templateParser = new TemplateParser(iocContext, treeLogger);
        this.dataFieldGenerator = new DataFieldGenerator(iocContext, j2CLUtils,
                templatedGeneratorUtils, treeLogger);
        this.dataFieldProcessor = new DataFieldProcessor(iocContext, treeLogger);
        this.stylesheetProcessor = new StylesheetProcessor(iocContext, templatedGeneratorUtils);
        this.eventHandlerTemplatedProcessor = new EventHandlerTemplatedProcessor(iocContext);
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

        TemplateDefinition templateDefinition =
                new TemplateDefinition(beanDefinition.getSimpleClassName());

        maybeHasNotGetMethod(beanDefinition, templateDefinition);
        classMetaInfo.addToDoCreateInstance(() -> "setAndInitTemplate(instance, interceptor)");
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
                        j2CLUtils.getMethodMangledName(executableElement.get());
                templateDefinition.setRootElementPropertyName(mangledName);
            }
        }
    }

    private void setAndInitTemplate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition,
                                    TemplateDefinition templateDefinition) {
        TypeElement type = MoreTypes.asTypeElement(beanDefinition.getType());
        String isElementTypeParameter = templateParser.getIsElementTypeParameter(type.getInterfaces());
        Templated templated = type.getAnnotation(Templated.class);

        TemplateContext context = new TemplateContext(TypeSimplifier.packageNameOf(type),
                TypeSimplifier.classNameOf(type), type.toString(), isElementTypeParameter, type.asType());

        TemplateSelector templateSelector = templateParser.getTemplateSelector(type, templated);

        if (templateSelector.inline) {
            context.setTemplateFileName("<inline>");
        } else {
            String fqTemplate =
                    TypeSimplifier.packageNameOf(type).replace('.', '/') + "/" + templateSelector.template;
            context.setTemplateFileName(fqTemplate);
        }

        org.jsoup.nodes.Element root = templateParser.parseTemplate(type, templateSelector);
        context.setRoot(templateParser.createRootElementInfo(root, type.toString()));

        List<DataElementInfo> dataElements =
                dataFieldGenerator.discoverDataFields(type, templateSelector, root);
        context.setDataElements(dataFieldProcessor.process(dataElements, context, root));

        List<EventHandlerInfo> eventElements =
                eventHandlerTemplatedProcessor.processEventHandlers(type, context);
        context.setEvents(eventElements);

        context.setStylesheet(stylesheetProcessor.resolveStylesheet(type, templated));

        code(classMetaInfo, beanDefinition, context, templateDefinition, root);

        String source = freemarkerTemplateGenerator.toSource(templateDefinition);
        classMetaInfo.addToBody(() -> source);
        logger.log(TreeLogger.Type.INFO, "Generated templated implementation [" + context.getSubclass()
                + "] for [" + context.getBase() + "]");
    }

    private void code(ClassMetaInfo builder, BeanDefinition beanDefinition,
                      TemplateContext templateContext, TemplateDefinition templateDefinition,
                      org.jsoup.nodes.Element root) {
        addImports(builder);
        stylesheetProcessor.processStylesheet(builder, templateContext, templateDefinition);

        setAttributes(templateContext, templateDefinition);
        setInnerHTML(templateContext, templateDefinition);

        dataFieldGenerator.generateCode(templateContext, templateDefinition);
        eventHandlerTemplatedProcessor.generateEventCode(beanDefinition, templateContext,
                templateDefinition);
        processI18nKeys(builder, root);
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

    private void setAttributes(TemplateContext templateContext,
                               TemplateDefinition templateDefinition) {
        templateContext.getRoot().getAttributes().stream()
                .map(attr -> new io.crysknife.ui.templates.generator.dto.Attribute(attr.getKey(),
                        attr.getValue().replaceAll("\\s+", " ").trim()))
                .forEach(a -> templateDefinition.getAttributes().add(a));
    }

    private void processI18nKeys(ClassMetaInfo classMetaInfo, org.jsoup.nodes.Element root) {
        org.jsoup.select.Elements i18nElements = root.select("[data-i18n-key]");
        if (i18nElements.isEmpty()) {
            return;
        }

        Set<TypeElement> bundleTypes =
                iocContext.getTypeElementsByAnnotation(TranslationBundle.class
                                .getCanonicalName());
        List<String> bundleClasses = new ArrayList<>();
        for (TypeElement te : bundleTypes) {
            bundleClasses.add(te.getQualifiedName().toString());
        }

        for (org.jsoup.nodes.Element element : i18nElements) {
            String key = element.attr("data-i18n-key");
            int dotIndex = key.indexOf('.');
            if (dotIndex <= 0 || dotIndex == key.length() - 1) {
                throw new GenerationException(
                        "Invalid data-i18n-key format: '" + key
                                + "'. Expected 'BundleName.methodName'");
            }
            String bundleSimpleName = key.substring(0, dotIndex);
            String methodName = key.substring(dotIndex + 1);

            // Find the bundle class
            String bundleQualified = null;
            for (String candidate : bundleClasses) {
                if (candidate.endsWith("." + bundleSimpleName)
                        || candidate.equals(bundleSimpleName)) {
                    bundleQualified = candidate;
                    break;
                }
            }
            if (bundleQualified == null) {
                throw new GenerationException(
                        "Unknown TranslationBundle: " + bundleSimpleName
                                + " referenced in data-i18n-key='" + key + "'");
            }

            // Verify method exists and has no parameters
            TypeElement bundleType = iocContext.getGenerationContext().getElements()
                    .getTypeElement(bundleQualified);
            boolean methodFound = false;
            for (javax.lang.model.element.Element enclosed : bundleType.getEnclosedElements()) {
                if (enclosed.getKind() == javax.lang.model.element.ElementKind.METHOD
                        && enclosed.getSimpleName().toString().equals(methodName)) {
                    ExecutableElement execMethod = (ExecutableElement) enclosed;
                    if (!execMethod.getParameters().isEmpty()) {
                        throw new GenerationException(
                                "data-i18n-key cannot reference method '" + methodName
                                        + "' with parameters; use @Inject instead");
                    }
                    methodFound = true;
                    break;
                }
            }
            if (!methodFound) {
                throw new GenerationException(
                        "No method '" + methodName + "' on TranslationBundle " + bundleSimpleName);
            }

            // Generate: element.textContent = new BundleImpl().method();
            String dataFieldId = element.attr("data-field");
            if (dataFieldId == null || dataFieldId.isEmpty()) {
                dataFieldId = element.attr("id");
            }
            if (dataFieldId == null || dataFieldId.isEmpty()) {
                throw new GenerationException(
                        "Element with data-i18n-key='" + key
                                + "' must have either data-field or id attribute");
            }
            String implQualified = bundleQualified + "Impl";
            String stmt =
                    "((elemental2.dom.HTMLElement) TemplateUtil.resolveElement(instance.getElement(), \""
                            + dataFieldId + "\")).textContent = new " + implQualified + "()."
                            + methodName + "();";
            classMetaInfo.addToDoInitInstance(() -> stmt);
        }
    }

    private void processOnDestroy(ClassMetaInfo builder) {
        builder.addToOnDestroy((Supplier<String>) () -> "eventHandlerRegistration.clear(instance);");
    }

    private boolean hasGetElement(BeanDefinition beanDefinition) {
        return ElementFilter
                .methodsIn(asElement(beanDefinition.getType()).getEnclosedElements()).stream()
                .map(MoreElements::asExecutable)
                .filter(method -> method.getSimpleName().toString().equals("getElement"))
                .anyMatch(method -> method.getParameters().isEmpty());
    }
}
