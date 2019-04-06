package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.ExecutableDefinition;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/4/19
 */
public class MethodDecoratorTypeProcessor extends TypeProcessor {

    @Override
    public void process(IOCContext context, IOCGenerator generator, Element element) {
        if (element.getKind().equals(ElementKind.METHOD)) {
            ExecutableElement method = MoreElements.asExecutable(element);
            TypeElement enclosingElement = MoreElements.asType(method.getEnclosingElement());
            BeanDefinition beanDefinition = getBeanDefinitionOrCreateAndGet(context, generator, enclosingElement);
            beanDefinition.addExecutableDefinition(generator, ExecutableDefinition.of(method, enclosingElement));
        }
    }
}

