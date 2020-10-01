package org.treblereel.gwt.crysknife.generator.scanner;

import javax.enterprise.inject.Produces;
import javax.lang.model.element.TypeElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/8/20
 */
public class ProducersScan {

  private IOCContext iocContext;

  public ProducersScan(IOCContext iocContext) {
    this.iocContext = iocContext;
  }

  public void scan() {
    iocContext.getMethodsByAnnotation(Produces.class.getCanonicalName()).forEach(producer -> {
      TypeElement parent = MoreElements.asType(producer.getEnclosingElement());
      TypeElement target = MoreTypes.asTypeElement(producer.getReturnType());
      BeanDefinition targetBeanDefinition = iocContext.getBeanDefinitionOrCreateAndReturn(target);
      targetBeanDefinition.getDependsOn()
          .add(iocContext.getBeanDefinitionOrCreateAndReturn(parent));
    });
  }
}
