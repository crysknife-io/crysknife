package org.treblereel.gwt.crysknife.generator;

import javax.lang.model.element.TypeElement;

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
        iocContext.getGenerators().forEach((meta, generator) -> {
            TypeElement annotation = iocContext.getGenerationContext().getElements().getTypeElement(meta.annotation);
            context.getRoundEnvironment().getElementsAnnotatedWith(annotation).forEach(element -> {
                TypeProcessorFactory.getTypeProcessor(meta).process(iocContext, generator, element);
            });
        });
    }
}
