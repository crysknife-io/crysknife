/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.treblereel.produces.qualifier;

import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
@Singleton
public class QualifierBeanProducerHolder {

  @Inject
  private QualifierBean qualifierBean;

  @Inject
  @Default
  private QualifierBean qualifierBeanDefault;

  @Inject
  @QualifierOne
  private QualifierBean qualifierBeanQualifierOne;

  private QualifierBean qualifierBeanConstructor;

  private QualifierBean qualifierBeanConstructorDefault;

    private QualifierBean qualifierBeanConstructorQualifierOne;

  @Inject
  public QualifierBeanProducerHolder(QualifierBean qualifierBeanConstructor, @Default QualifierBean qualifierBeanConstructorDefault, @QualifierOne QualifierBean qualifierBeanConstructorQualifierOne) {
    this.qualifierBeanConstructor = qualifierBeanConstructor;
    this.qualifierBeanConstructorDefault = qualifierBeanConstructorDefault;
    this.qualifierBeanConstructorQualifierOne = qualifierBeanConstructorQualifierOne;
  }

  public QualifierBean getQualifierBean() {
    return qualifierBean;
  }

  public QualifierBean getQualifierBeanDefault() {
    return qualifierBeanDefault;
  }

  public QualifierBean getQualifierBeanQualifierOne() {
    return qualifierBeanQualifierOne;
  }

  public QualifierBean getQualifierBeanConstructor() {
    return qualifierBeanConstructor;
  }

  public QualifierBean getQualifierBeanConstructorDefault() {
    return qualifierBeanConstructorDefault;
  }

  public QualifierBean getQualifierBeanConstructorQualifierOne() {
    return qualifierBeanConstructorQualifierOne;
  }
}
