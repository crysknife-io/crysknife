package org.treblereel.gwt.crysknife.generator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.context.GenerationContext;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.processor.TypeProcessorFactory;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
public class ComponentScanner {

    private final IOCContext iocContext;
    private final GenerationContext context;

    public ComponentScanner(IOCContext iocContext, GenerationContext context) {
        this.iocContext = iocContext;
        this.context = context;
    }

    public void scan() {

        Set<TypeElement> annotations = new HashSet<>();

        iocContext.getGenerators()
                .keySet()
                .stream()
                .sorted(Comparator.comparing(h -> iocContext
                        .getGenerators()
                        .get(h)
                        .stream()
                        .findFirst()
                        .get()
                        .getClass()
                        .getAnnotation(Generator.class)
                        .priority()))
                .forEach(meta -> {
                    TypeElement annotation = context.getElements().getTypeElement(meta.annotation);
                    annotations.add(annotation);
                });

        Map<TypeElement, HashSet<Element>> elements = new HashMap<>();
        annotations.forEach(annotation -> {
            if (!elements.containsKey(annotation)) {
                elements.put(annotation, new HashSet<>());
            }
            context.getRoundEnvironment().getElementsAnnotatedWith(annotation).forEach(element -> {
                elements.get(annotation).add(element);
            });
        });

        TypeElement object = iocContext.getGenerationContext()
                .getElements()
                .getTypeElement(Object.class.getCanonicalName());

        // all types
        elements.forEach((annotation, points) -> {
            iocContext.getGenerators().keySet()
                    .stream().filter(meta -> (meta.annotation.equals(annotation.getQualifiedName().toString())
                    && meta.exactType.equals(object)))
                    .forEach(meta -> {
                        points.stream().forEach(point -> {
                            IOCGenerator generator = iocContext.getGenerators().get(meta).stream().findFirst().get();
                            TypeProcessorFactory.getTypeProcessor(meta, generator, point)
                                    .ifPresent(processor -> processor.process(iocContext, point));
                        });
                    });
        });

        //exactly on type
        Set<IOCContext.IOCGeneratorMeta> metas = iocContext.getGenerators().keySet()
                .stream().filter(meta -> !meta.exactType.equals(object)).collect(Collectors.toSet());

        metas.forEach(meta -> {
            TypeElement annotation = iocContext.getGenerationContext().getProcessingEnvironment().getElementUtils().getTypeElement(meta.annotation);
            elements.get(annotation).stream().filter(e -> e.getKind().equals(ElementKind.FIELD))
                    .filter(elm -> MoreElements.asVariable(elm).asType().equals(meta.exactType.asType()))
                    .forEach(elm -> {
                        TypeProcessorFactory.getTypeProcessor(meta, iocContext.getGenerators().get(meta).stream().findFirst().get(), elm)
                                .ifPresent(processor -> processor.process(iocContext, elm));
                    });
        });
    }
}
