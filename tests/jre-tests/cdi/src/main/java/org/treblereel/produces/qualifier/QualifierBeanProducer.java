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

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

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
        return "Default";
      }
    };
  }

  @Produces
  @Dependent
  @QualifierOne
  public QualifierBean getSimpleQualifierBeanQualifierOne() {
    return new QualifierBean() {
      @Override
      public String say() {
        return "QualifierOne";
      }
    };
  }
}
