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

import io.crysknife.client.BeanManager;
import io.crysknife.client.ManagedInstance;
import org.treblereel.injection.dependent.SimpleBeanDependent;
import org.treblereel.produces.qualifier.QualifierBean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/25/21
 */
@ApplicationScoped
public class ManagedInstanceBean {

  @Inject
  private BeanManager beanManager;

  @Inject
  private ManagedInstance<ComponentIface> managedInstanceBean;

  @Inject
  private Instance<ComponentIface> InstanceBean;

  @Inject
  private Instance<SimpleBeanDependent> bean;

  @Inject
  private Instance<QualifierBean> bean2;

  public ManagedInstance<ComponentIface> getManagedInstanceBean() {
    return managedInstanceBean;
  }

  public Instance<ComponentIface> getInstanceBean() {
    return InstanceBean;
  }

  public Instance<SimpleBeanDependent> getBean() {
    return bean;
  }

  public Instance<QualifierBean> getBean2() {
    return bean2;
  }
}
