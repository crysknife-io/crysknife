/*
 * Copyright © 2021 Treblereel
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

package org.treblereel.injection.qualifiers.specializes;

import org.treblereel.injection.managedinstance.SimpleBean;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 8/26/21
 */
@Specializes
@Singleton
public class SpecializesBeanImpl extends BeanImpl {

  @Inject
  private SimpleBean localSimpleBean;

  public SimpleBean getLocalSimpleBean() {
    return localSimpleBean;
  }

}
