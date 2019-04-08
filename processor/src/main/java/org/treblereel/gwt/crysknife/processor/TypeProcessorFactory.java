package org.treblereel.gwt.crysknife.processor;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
public class TypeProcessorFactory {

    public static Optional<TypeProcessor> getTypeProcessor(IOCContext.IOCGeneratorMeta meta, Element element) {
        if (element.getKind().equals(ElementKind.FIELD)) {
            TypeMirror mirror = MoreElements.asVariable(element).asType();
            TypeElement type = MoreTypes.asTypeElement(mirror);
            if (type.equals(meta.exactType)) {
                return Optional.of(new ExactTypeProcessor());
            }
        }

        switch (meta.wiringElementType) {
            case DEPENDENT_BEAN:
                return Optional.of(new DependentTypeProcessor());
            case PRODUCER_ELEMENT:
                return Optional.of(new ProducerTypeProcessor());
            case CLASS_DECORATOR:
                return Optional.of(new ClassDecoratorTypeProcessor());
            case METHOD_DECORATOR:
                return Optional.of(new MethodDecoratorTypeProcessor());
            case PARAMETER:
                return Optional.of(new ParameterTypeProcessor());
            default:
                return Optional.empty();
        }
        // throw new IllegalArgumentException("Unable to find TypeProcessor for " + element + " in  "
        //                                            + element.getEnclosingElement().toString() + " of " + meta.wiringElementType);
    }
}
