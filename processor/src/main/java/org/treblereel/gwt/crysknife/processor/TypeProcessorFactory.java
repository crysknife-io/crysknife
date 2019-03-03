package org.treblereel.gwt.crysknife.processor;

import org.treblereel.gwt.crysknife.generator.context.IOCContext;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
public class TypeProcessorFactory {

    public static TypeProcessor getTypeProcessor(IOCContext.IOCGeneratorMeta meta) {

        switch (meta.wiringElementType) {
            case DEPENDENT_BEAN:
                return new DependentTypeProcessor();
            case PRODUCER_ELEMENT:
                return new ProducerTypeProcessor();
            case METHOD_DECORATOR:
                return new MethodDecoratorTypeProcessor();
        }
        throw new IllegalArgumentException();
    }
}
