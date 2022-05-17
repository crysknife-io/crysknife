/*
 * Copyright (C) 2021
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

package io.crysknife.client.internal.proxy;

import io.crysknife.client.BeanManager;
import io.crysknife.client.internal.BeanFactory;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/30/21
 */
public abstract class ProxyBeanFactory<T> extends BeanFactory<T> {

  protected ProxyBeanFactory(BeanManager beanManager) {
    super(beanManager);
  }

  public abstract void dependantBeanReady(Class clazz);
}
