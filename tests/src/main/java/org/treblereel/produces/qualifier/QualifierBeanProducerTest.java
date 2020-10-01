package org.treblereel.produces.qualifier;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
@Singleton
public class QualifierBeanProducerTest {

  @Inject
  private QualifierBean qualifierBean;

  public QualifierBean getQualifierBean() {
    return qualifierBean;
  }

  public void setQualifierBean(QualifierBean qualifierBean) {
    this.qualifierBean = qualifierBean;
  }
}
