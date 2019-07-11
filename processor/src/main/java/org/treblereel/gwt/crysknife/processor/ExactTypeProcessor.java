package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/31/19
 */
public class ExactTypeProcessor extends TypeProcessor {

    protected ExactTypeProcessor(IOCGenerator generator) {
        super(generator);
    }

    @Override
    public void process(IOCContext context, Element element) {
        if (element.getKind().isField() || element.getKind().isClass()) {
            TypeMirror mirror = (element.getKind().isField() ?
                    MoreElements.asVariable(element).asType() :
                    element.asType());
            TypeElement typeElement = MoreTypes.asTypeElement(mirror);
            BeanDefinition beanDefinition = context.getBeanDefinitionOrCreateAndReturn(typeElement);
            if (typeElement.getTypeParameters().size() > 0) {
                TypeMirror type = element.asType();
                beanDefinition.getDeclaredTypes().add(MoreTypes.asDeclared(type));
            }
            beanDefinition.setGenerator(generator);
        }
    }

}