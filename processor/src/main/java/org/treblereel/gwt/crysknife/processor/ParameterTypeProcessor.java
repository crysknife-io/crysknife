package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.ExecutableDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/5/19
 */
public class ParameterTypeProcessor extends TypeProcessor {

    protected ParameterTypeProcessor(IOCGenerator generator) {
        super(generator);
    }

    @Override
    public void process(IOCContext context, Element element) {
        if (element.getKind().equals(ElementKind.PARAMETER)) {
            VariableElement parameter = MoreElements.asVariable(element);
            ExecutableElement method = MoreElements.asExecutable(parameter.getEnclosingElement());

            TypeElement enclosingElement = MoreElements.asType(method.getEnclosingElement());
            BeanDefinition beanDefinition = context.getBeanDefinitionOrCreateAndReturn(enclosingElement);
            beanDefinition.addExecutableDefinition(generator, ExecutableDefinition.of(method, enclosingElement));
        }
    }
}