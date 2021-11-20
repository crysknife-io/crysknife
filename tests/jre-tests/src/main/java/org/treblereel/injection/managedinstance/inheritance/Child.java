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

package org.treblereel.injection.managedinstance.inheritance;

import io.crysknife.client.ManagedInstance;
import org.treblereel.injection.inheritance.BeanChild;
import org.treblereel.injection.managedinstance.SimpleBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Child extends Parent<SimpleBean, BeanChild> {

  public ManagedInstance<BeanChild> instance2;

  @Inject
  public Child(ManagedInstance<BeanChild> instance) {
    this.instance2 = instance;
  }

  public SimpleBean get() {
    return instance.get();
  }

  public BeanChild get2() {
    return instance2.get();
  }
}
