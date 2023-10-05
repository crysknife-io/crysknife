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

package org.treblereel.injection.qualifiers;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.crysknife.annotation.Lazy;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/14/19
 */
@Singleton
public class QualifierConstructorInjection {

  public QualifierBean qualifierBeanOne;

  public QualifierBean qualifierBeanTwo;

  @Inject
  @Lazy
  public QualifierBean qualifier;

  @Inject
  public QualifierConstructorInjection(@QualifierTwo @Lazy final QualifierBean qualifierBeanTwo,
      @QualifierOne final QualifierBean qualifierBeanOne) {
    this.qualifierBeanOne = qualifierBeanOne;
    this.qualifierBeanTwo = qualifierBeanTwo;
  }
}
