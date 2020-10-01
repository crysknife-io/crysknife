package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.ExecutableDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/4/19
 */
public class MethodDecoratorTypeProcessor extends TypeProcessor {

  protected MethodDecoratorTypeProcessor(IOCGenerator generator) {
    super(generator);
  }

  @Override
  public void process(IOCContext context, Element element) {
    if (element.getKind().equals(ElementKind.METHOD)) {
      ExecutableElement method = MoreElements.asExecutable(element);
      TypeElement enclosingElement = MoreElements.asType(method.getEnclosingElement());
      BeanDefinition beanDefinition = context.getBeanDefinitionOrCreateAndReturn(enclosingElement);
      beanDefinition.addExecutableDefinition(generator,
          ExecutableDefinition.of(method, enclosingElement));
    }
  }
}
