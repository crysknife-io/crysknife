package org.treblereel.injection.qualifiers;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/14/19
 */
@Singleton
public class QualifierFieldInjection {

  @Inject
  @QualifierOne
  public QualifierBean qualifierBeanOne;

  @Inject
  @QualifierTwo
  public QualifierBean qualifierBeanTwo;

  @Inject
  @Default
  public QualifierBean qualifierBeanDefault;

  public QualifierBean getQualifierBeanOne() {
    return qualifierBeanOne;
  }

  public QualifierBean getQualifierBeanTwo() {
    return qualifierBeanTwo;
  }

  public QualifierBean getQualifierBeanDefault() {
    return qualifierBeanDefault;
  }
}
