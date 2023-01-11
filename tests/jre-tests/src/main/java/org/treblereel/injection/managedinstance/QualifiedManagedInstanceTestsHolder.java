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

package org.treblereel.injection.managedinstance;

import io.crysknife.client.ManagedInstance;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class QualifiedManagedInstanceTestsHolder {

  public final ManagedInstance<ComponentIface> c_bean1;
  public final ManagedInstance<ComponentIface> c_bean2;

  @Inject
  @ComponentQualifierOne
  public ManagedInstance<ComponentIface> f_bean1;
  @Inject
  @ComponentQualifierTwo
  public ManagedInstance<ComponentIface> f_bean2;

  @Inject
  public QualifiedManagedInstanceTestsHolder(
      @ComponentQualifierOne ManagedInstance<ComponentIface> c_bean1,
      @ComponentQualifierTwo ManagedInstance<ComponentIface> c_bean2) {
    this.c_bean1 = c_bean1;
    this.c_bean2 = c_bean2;
  }
}
