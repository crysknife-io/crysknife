package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.definition.ProducerDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */

public class ProducerTypeProcessor extends TypeProcessor {

    protected ProducerTypeProcessor(IOCGenerator generator) {
        super(generator);
    }

    @Override
    public void process(IOCContext context, Element element) {
        if (element.getKind().equals(ElementKind.METHOD)) {
            ExecutableElement method = MoreElements.asExecutable(element);
            Element theReturn = MoreTypes.asElement(method.getReturnType());

            ProducerDefinition producerDefinition = ProducerDefinition.of(method, MoreElements.asType(method.getEnclosingElement()));
            producerDefinition.setGenerator(generator);
            context.getBeans().put(MoreElements.asType(theReturn), producerDefinition);
        }
    }
}
