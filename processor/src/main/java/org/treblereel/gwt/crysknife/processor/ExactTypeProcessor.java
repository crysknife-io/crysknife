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

    @Override
    public void process(IOCContext context, IOCGenerator generator, Element element) {
        if (element.getKind().equals(ElementKind.FIELD)) {
            TypeMirror mirror = MoreElements.asVariable(element).asType();
            TypeElement typeElement = MoreTypes.asTypeElement(mirror);
            BeanDefinition beanDefinition = getBeanDefinitionOrCreateAndGet(context, generator, typeElement);
            if (typeElement.getTypeParameters().size() > 0) {
                TypeMirror type = element.asType();
                beanDefinition.getDeclaredTypes().add(MoreTypes.asDeclared(type));
            }
            beanDefinition.addGenerator(generator);
        }
    }
}