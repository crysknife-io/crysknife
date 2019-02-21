package org.treblereel.gwt.crysknife;

import com.google.auto.service.AutoService;
import org.treblereel.gwt.crysknife.client.Application;
import org.treblereel.gwt.crysknife.client.ComponentScan;
import org.treblereel.gwt.crysknife.internal.BeanDefinition;
import org.treblereel.gwt.crysknife.internal.GenerationContext;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"org.treblereel.gwt.crysknife.client.Application",
        "javax.inject.Inject",
        "javax.inject.Singleton",
        "org.treblereel.gwt.crysknife.client.ComponentScan"})
public class ApplicationProcessor extends AbstractProcessor {

    private Set<String> packages;
    private Set<TypeElement> injections = new HashSet<>();
    private Set<TypeElement> singletons = new HashSet<>();
    private Set<ExecutableElement> postConstructors = new HashSet<>();
    private GenerationContext context;

    private TypeElement application;

    private Map<TypeElement, BeanDefinition> definitions = new HashMap<>();

    public static Set<Element> getAnnotatedElements(
            Elements elements,
            TypeElement type,
            Class<? extends Annotation> annotation) {
        Set<Element> found = new HashSet<>();
        for (Element e : elements.getAllMembers(type)) {
            if (e.getAnnotation(annotation) != null)
                found.add(e);
        }
        return found;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (annotations.isEmpty()) {
            return false;
        }


        processApplicationAnnotation((Set<TypeElement>) roundEnvironment.getElementsAnnotatedWith(Application.class));
        processComponentScanAnnotation((Set<TypeElement>) roundEnvironment.getElementsAnnotatedWith(ComponentScan.class));

        context = new GenerationContext(roundEnvironment, processingEnv, packages, application);
        new BootstrapperGenerator(context, roundEnvironment, processingEnv, packages, application).generate();
        new FactoryGenerator(context, definitions, roundEnvironment, processingEnv).generate();


        return true;
    }

    private void processComponentScanAnnotation(Set<TypeElement> elements) {
        packages = new HashSet<>();

        elements.forEach(componentScan -> {
            String[] values = componentScan.getAnnotation(ComponentScan.class).value();
            for (String aPackage : values) {
                packages.add(aPackage);
            }
        });
    }

    private void processApplicationAnnotation(Set<TypeElement> elements) {
        if (elements.size() == 0) {
            throw new Error("no class annotated with @Application detected");
        }

        if (elements.size() > 1) {
            throw new Error("there must only one class annotated with @Application");
        }
        this.application = elements.stream().findFirst().get();//TODO

        elements.forEach(injectAnnotated -> {
            System.out.println("bean " + injectAnnotated.getQualifiedName());
        });
    }
}
