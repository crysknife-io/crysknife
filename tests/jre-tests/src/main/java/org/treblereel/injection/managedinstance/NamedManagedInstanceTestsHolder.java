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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
public class NamedManagedInstanceTestsHolder {

  public final ManagedInstance<NamedIface> c_bean1;
  public final ManagedInstance<NamedIface> c_bean2;

  @Inject
  @Named("NamedBeanOne")
  public ManagedInstance<NamedIface> f_bean1;
  @Inject
  @Named("NamedBeanTwo")
  public ManagedInstance<NamedIface> f_bean2;

  @Inject
  public NamedManagedInstanceTestsHolder(@Named("NamedBeanOne") ManagedInstance<NamedIface> c_bean1,
      @Named("NamedBeanTwo") ManagedInstance<NamedIface> c_bean2) {
    this.c_bean1 = c_bean1;
    this.c_bean2 = c_bean2;
  }

}
