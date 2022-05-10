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

import javax.enterprise.context.Dependent;
import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/25/21
 */
public abstract class BeanFactory<T> {

  protected AbstractBeanManager beanManager;

  protected SyncBeanDef beanDef;

  protected T instance;
  private T incompleteInstance;
  private boolean initialized = false;

  protected BeanFactory(BeanManager beanManager) {
    this.beanManager = (AbstractBeanManager) beanManager;
  }

  public T getIncompleteInstance() {
    return incompleteInstance;
  }

  protected void setIncompleteInstance(final T instance) {
    incompleteInstance = instance;
  }

  public abstract <T> T getInstance();

  public void initInstance(T instance) {
    if (beanDef.getScope().equals(Dependent.class) || !initialized) {
      doInitInstance(instance);
      initialized = true;
    }
  }

  protected void doInitInstance(T instance) {

  }

  public <T> T createNewInstance() {
    if (instance != null) {
      createInstance();
    }
    return (T) instance;
  }

  protected <T> T createInstance() {
    throw new UnsupportedOperationException(
        "The factory, " + getClass().getSimpleName() + ", only supports contextual instances.");
  }

  protected <T> T createInstanceInternal() {
    T instance = createInstance();
    return addBeanInstanceToPool(instance, this);
  }

  public T createContextualInstance(final Class<?>[] typeArgs, final Annotation[] qualifiers) {
    throw new UnsupportedOperationException(
        "The factory, " + getClass().getSimpleName() + ", does not support contextual instances.");
  }

  protected void onDestroy() {

  }

  void onDestroyInternal() {
    onDestroy();
    this.instance = null;
  }

  protected <T> T addBeanInstanceToPool(Object instance, BeanFactory factory) {
    return (T) beanManager.addBeanInstanceToPool(instance, factory);
  }

}
