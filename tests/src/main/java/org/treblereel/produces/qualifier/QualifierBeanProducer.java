package org.treblereel.produces.qualifier;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class QualifierBeanProducer {

  @Produces
  @Dependent
  public QualifierBean getSimpleQualifierBean() {
    return new QualifierBean() {
      @Override
      public String say() {
        return this.getClass().getSimpleName();
      }
    };
  }
}
