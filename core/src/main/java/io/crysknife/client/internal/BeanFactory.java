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

package io.crysknife.client.internal;

import io.crysknife.client.BeanManager;
import io.crysknife.client.SyncBeanDef;

import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/25/21
 */
public abstract class BeanFactory<T> {

  protected BeanManager beanManager;

  protected SyncBeanDef beanDef;

  private T incompleteInstance;

  protected T instance;

  protected BeanFactory(BeanManager beanManager) {
    this.beanManager = beanManager;
  }

  public T getIncompleteInstance() {
    return incompleteInstance;
  }

  protected void setIncompleteInstance(final T instance) {
    incompleteInstance = instance;
  }

  public abstract <T> T getInstance();

  public void initInstance() {}

  public <T> T createInstance() {
    throw new UnsupportedOperationException(
        "The factory, " + getClass().getSimpleName() + ", only supports contextual instances.");
  }

  public T createContextualInstance(final Class<?>[] typeArgs, final Annotation[] qualifiers) {
    throw new UnsupportedOperationException(
        "The factory, " + getClass().getSimpleName() + ", does not support contextual instances.");
  }

}
