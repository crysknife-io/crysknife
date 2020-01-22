package org.treblereel.gwt.crysknife;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.BeanManager;
import org.treblereel.gwt.crysknife.client.ComponentScan;
import org.treblereel.gwt.crysknife.generator.ComponentInjectionResolverScanner;
import org.treblereel.gwt.crysknife.generator.ComponentScanner;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.QualifiersScan;
import org.treblereel.gwt.crysknife.generator.WiringElementType;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.graph.Graph;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "org.treblereel.gwt.crysknife.client.Application",
        "javax.inject.Inject",
        "javax.inject.Singleton",
        "org.treblereel.gwt.crysknife.client.ComponentScan"})
public class ApplicationProcessor extends AbstractProcessor {

    private IOCContext iocContext;
    private Set<String> packages;
    private GenerationContext context;
    private TypeElement application;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (annotations.isEmpty()) {
            return false;
        }

        context = new GenerationContext(roundEnvironment, processingEnv);
        iocContext = new IOCContext(context);

        Optional<TypeElement> maybeApplication = processApplicationAnnotation(iocContext);
        if (!maybeApplication.isPresent()) {
            return true;
        }
        this.application = maybeApplication.get();

        processComponentScanAnnotation();
        initAndRegisterGenerators();
        processQualifiersScan();
        processComponentScan();
        processInjectionScan();
        processGraph();
        processPrepareForGenerationTasks();

        new FactoryGenerator(iocContext, context).generate();
        new BeanInfoGenerator(iocContext, context).generate();
        new BeanManagerGenerator(iocContext, context).generate();
        return false;
    }

    private void processPrepareForGenerationTasks() {
        TypeElement beanManager = iocContext.getGenerationContext()
                .getProcessingEnvironment()
                .getElementUtils()
                .getTypeElement("org.treblereel.gwt.crysknife.client.BeanManager");

        Optional<TypeElement> ifPresent = iocContext.getBeans().keySet()
                .stream().filter(dep -> dep.equals(beanManager)).findFirst();
        if (!ifPresent.isPresent()) {
            BeanDefinition beanManagerDefinition = iocContext.getBeanDefinitionOrCreateAndReturn(beanManager);

            TypeElement type = iocContext
                    .getGenerationContext()
                    .getElements()
                    .getTypeElement(BeanManager.class.getCanonicalName());
            //TODO this should be replaced
            IOCContext.IOCGeneratorMeta meta = new IOCContext.IOCGeneratorMeta(Inject.class.getCanonicalName(),
                                                                               type,
                                                                               WiringElementType.FIELD_TYPE);
            beanManagerDefinition.setGenerator(iocContext.getGenerators().get(meta).stream().findFirst().get());
            iocContext.getBeans().put(beanManager, beanManagerDefinition);
        }
    }

    private void processQualifiersScan() {
        new QualifiersScan(iocContext).process();
    }

    private void processGraph() {
        new Graph(iocContext).process(application);
    }

    private void initAndRegisterGenerators() {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
            ClassInfoList routeClassInfoList = scanResult.getClassesWithAnnotation(Generator.class.getCanonicalName());
            for (ClassInfo routeClassInfo : routeClassInfoList) {
                try {
                    ((IOCGenerator) Class.forName(routeClassInfo.getName()).newInstance()).register(iocContext);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new Error(e);
                }
            }
        }
    }

    private void processInjectionScan() {
        new ComponentInjectionResolverScanner(iocContext).scan();
    }

    private void processComponentScan() {
        new ComponentScanner(iocContext, context).scan();
    }

    private void processComponentScanAnnotation() {
        packages = new HashSet<>();
        context.getRoundEnvironment()
                .getElementsAnnotatedWith(ComponentScan.class)
                .forEach(componentScan -> {
                    String[] values = componentScan.getAnnotation(ComponentScan.class).value();
                    for (String aPackage : values) {
                        packages.add(aPackage);
                    }
                });

        if (packages.isEmpty()) {
            packages.add(MoreElements.getPackage(application).getQualifiedName().toString());
        }
    }

    private Optional<TypeElement> processApplicationAnnotation(IOCContext iocContext) {
        Set<TypeElement> applications = (Set<TypeElement>) iocContext.getGenerationContext()
                .getRoundEnvironment()
                .getElementsAnnotatedWith(Application.class);

        if (applications.size() == 0) {
            context.getProcessingEnvironment()
                    .getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "No class annotated with @Application detected\"");
            throw new Error();
        }

        if (applications.size() > 1) {
            context.getProcessingEnvironment()
                    .getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "There is must be only one class annotated with @Application\"");
            throw new Error();
        }
        return applications.stream().findFirst();
    }
}