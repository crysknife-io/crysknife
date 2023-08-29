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
package io.crysknife.client.internal.proxy;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import elemental2.core.Reflect;
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.internal.BeanFactory;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 1/1/20
 */
@SuppressWarnings("rawtypes, unchecked")
public final class OnFieldAccessed implements BiFunction<Object, String, Object> {

  private final Supplier<InstanceFactory> supplier;
  private final BeanFactory beanFactory;

  public OnFieldAccessed(BeanFactory beanFactory, Supplier<InstanceFactory> supplier) {
    this.supplier = supplier;
    this.beanFactory = beanFactory;
  }

  @Override
  public Object apply(Object o, String propertyKey) {
    if (Reflect.get(o, propertyKey) == null) {
      Reflect.set(o, propertyKey, beanFactory.addDependencyField(o, supplier.get()));
    }
    return Reflect.get(o, propertyKey);
  }
}
