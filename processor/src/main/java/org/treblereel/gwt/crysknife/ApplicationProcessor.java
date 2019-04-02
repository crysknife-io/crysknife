package org.treblereel.gwt.crysknife;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.ComponentScan;
import org.treblereel.gwt.crysknife.generator.BeanManagerProducerGenerator;
import org.treblereel.gwt.crysknife.generator.BootstrapperGenerator;
import org.treblereel.gwt.crysknife.generator.ComponentInjectionResolverScanner;
import org.treblereel.gwt.crysknife.generator.ComponentScanner;
import org.treblereel.gwt.crysknife.generator.DependentGenerator;
import org.treblereel.gwt.crysknife.generator.EventProducerGenerator;
import org.treblereel.gwt.crysknife.generator.ObservesGenerator;
import org.treblereel.gwt.crysknife.generator.PostConstructGenerator;
import org.treblereel.gwt.crysknife.generator.ProducesGenerator;
import org.treblereel.gwt.crysknife.generator.SingletonGenerator;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.graph.Graph;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"org.treblereel.gwt.crysknife.client.Application",
        "javax.inject.Inject",
        "javax.inject.Singleton",
        "org.treblereel.gwt.crysknife.client.ComponentScan"})
public class ApplicationProcessor extends AbstractProcessor {

    private final List<String> orderedBeans = new LinkedList<>();
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

        addPreBuildGenerators();
        externalGeneratorslookup(context);
        processComponentScan();
        processInjectionScan();
        processGraph();

        new FactoryGenerator(iocContext, context).generate();
        new BeanManagerGenerator(iocContext, context).generate();
        return true;
    }

    private void processGraph() {
        new Graph(iocContext).process(application);
    }

    //TODO
    private void externalGeneratorslookup(GenerationContext context) {
        Set<TypeElement> generators = (Set<TypeElement>) context.getRoundEnvironment().getElementsAnnotatedWith(Generator.class);
    }

    private void processInjectionScan() {
        new ComponentInjectionResolverScanner(iocContext).scan();
    }

    private void processComponentScan() {
        new ComponentScanner(iocContext, context).scan();
    }

    private void addPreBuildGenerators() {

        new SingletonGenerator().register(iocContext);
        new DependentGenerator().register(iocContext);
        new PostConstructGenerator().register(iocContext);
        new ProducesGenerator().register(iocContext);
        new BeanManagerProducerGenerator().register(iocContext);
        new EventProducerGenerator().register(iocContext);
        new BootstrapperGenerator().register(iocContext);
        new ObservesGenerator().register(iocContext);
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
            System.out.println("no class annotated with @Application detected");
        }

        if (applications.size() > 1) {
            System.out.println("there must only one class annotated with @Application");
        }
        return applications.stream().findFirst();
    }
}
