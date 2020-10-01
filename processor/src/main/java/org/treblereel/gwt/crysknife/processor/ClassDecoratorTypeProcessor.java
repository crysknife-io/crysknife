package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/7/19
 */
public class ClassDecoratorTypeProcessor extends TypeProcessor {

  protected ClassDecoratorTypeProcessor(IOCGenerator generator) {
    super(generator);
  }

  @Override
  public void process(IOCContext context, Element element) {
    if (MoreElements.isType(element)) {
      BeanDefinition beanDefinition =
          context.getBeanDefinitionOrCreateAndReturn(MoreElements.asType(element));
      context.getBeans().get(MoreElements.asType(element)).addDecorator(generator, beanDefinition);
    }
  }
}
