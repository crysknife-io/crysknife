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

package io.crysknife.ui.navigation.client.local.spi;

import io.crysknife.client.BeanManager;
import io.crysknife.ui.navigation.client.shared.NavigationEvent;

import javax.enterprise.event.Event;

/**
 * Fake implementation, ll be excluded on package/install
 *
 * @author Dmitrii Tikhomirov Created by treblereel 3/1/20
 */
public class NavigationGraphImpl extends NavigationGraph {

  public NavigationGraphImpl(BeanManager beanManager, Event<NavigationEvent> event) {
    super(beanManager, event);
  }
}
