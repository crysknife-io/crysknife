package org.treblereel.gwt.crysknife.processor;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
public class TypeProcessorFactory {

    public static Optional<TypeProcessor> getTypeProcessor(IOCContext.IOCGeneratorMeta meta, IOCGenerator generator, Element element) {
        if (element.getKind().isField() || element.getKind().isClass()) {
            TypeMirror type = (element.getKind().isField() ?
                    MoreElements.asVariable(element).asType() :
                    element.asType());
            if (type.equals(meta.exactType.asType())) {
                return Optional.of(new ExactTypeProcessor(generator));
            }
        }

        switch (meta.wiringElementType) {
            case DEPENDENT_BEAN:
                return Optional.of(new DependentTypeProcessor(generator));
            case PRODUCER_ELEMENT:
                return Optional.of(new ProducerTypeProcessor(generator));
            case CLASS_DECORATOR:
                return Optional.of(new ClassDecoratorTypeProcessor(generator));
            case METHOD_DECORATOR:
                return Optional.of(new MethodDecoratorTypeProcessor(generator));
            case PARAMETER:
                return Optional.of(new ParameterTypeProcessor(generator));
            default:
                return Optional.empty();
        }
        // throw new IllegalArgumentException("Unable to find TypeProcessor for " + element + " in  "
        //                                            + element.getEnclosingElement().toString() + " of " + meta.wiringElementType);
    }
}
