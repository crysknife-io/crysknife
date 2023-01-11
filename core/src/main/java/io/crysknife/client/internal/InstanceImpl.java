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
package io.crysknife.client.internal;

import io.crysknife.client.InstanceFactory;
import io.crysknife.client.SyncBeanDef;
import jakarta.enterprise.inject.Instance;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * TODO this class must be refactored
 *
 * @author Dmitrii Tikhomirov Created by treblereel 3/29/19
 */
public class InstanceImpl<T> implements Instance<T>, InstanceFactory<T> {

  private Supplier<T> provider;

  public InstanceImpl(T provider) {
    this.provider = () -> provider;
  }

  public InstanceImpl(SyncBeanDef<T> provider) {
    this.provider = () -> provider.getInstance();
  }

  @Override
  public T get() {
    return provider.get();
  }

  @Override
  public T getInstance() {
    return provider.get();
  }

  @Override
  public Instance<T> select(Annotation... var1) {
    return null;
  }

  @Override
  public <U extends T> Instance<U> select(Class<U> var1, Annotation... var2) {
    return null;
  }

  @Override
  public boolean isUnsatisfied() {
    return false;
  }

  @Override
  public boolean isAmbiguous() {
    return false;
  }

  @Override
  public void destroy(T var1) {}

  @Override
  public Iterator<T> iterator() {
    return null;
  }

}
