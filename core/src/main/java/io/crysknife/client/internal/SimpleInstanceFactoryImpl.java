/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package io.crysknife.client.internal;

import io.crysknife.client.InstanceFactory;
import io.crysknife.client.internal.weak.WeakReference;

public class SimpleInstanceFactoryImpl<T> implements InstanceFactory<T> {

  private WeakReference<T> instance;

  public SimpleInstanceFactoryImpl(T instance) {
    this.instance = new WeakReference<>(instance);
  }

  @Override
  public T getInstance() {
    return instance.get();
  }
}
