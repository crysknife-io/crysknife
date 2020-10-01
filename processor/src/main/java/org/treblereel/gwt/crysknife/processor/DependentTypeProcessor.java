package org.treblereel.gwt.crysknife.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.IOCGenerator;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/4/19
 */
public class DependentTypeProcessor extends TypeProcessor {

  protected DependentTypeProcessor(IOCGenerator generator) {
    super(generator);
  }

  @Override
  public void process(IOCContext context, Element element) {
    if (MoreElements.isType(element)) {
      TypeElement typeElement = MoreElements.asType(element);
      BeanDefinition beanDefinition = context.getBeanDefinitionOrCreateAndReturn(typeElement);
      beanDefinition.setGenerator(generator);
    }
  }

}
