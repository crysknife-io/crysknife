package org.treblereel.gwt.crysknife.processor;

import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.WiringElementType;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
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

            BeanDefinition bean = context.getBeanDefinitionOrCreateAndReturn(MoreElements.asType(method.getEnclosingElement()));

            TypeElement obj = context.getGenerationContext().getElements().getTypeElement(Object.class.getCanonicalName());

            IOCContext.IOCGeneratorMeta meta = new IOCContext.IOCGeneratorMeta(Singleton.class.getCanonicalName(),
                                                                               obj,
                                                                               WiringElementType.BEAN);

            bean.setGenerator(context.getGenerators().get(meta).stream().findFirst().get());
            context.getBeans().put(MoreElements.asType(method.getEnclosingElement()), bean);
        }
    }
}
