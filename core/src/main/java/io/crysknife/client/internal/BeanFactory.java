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
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.SyncBeanDef;
import jakarta.enterprise.context.Dependent;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/25/21
 */
public abstract class BeanFactory<T> {

  protected AbstractBeanManager beanManager;

  protected SyncBeanDef<T> beanDef;

  protected T instance;
  private T incompleteInstance;
  private boolean initialized = false;

  // It's ok to use HashSet here because we don't care about order and we don't need to synchronize.
  // App is single threaded,
  // so only one thread will access this, because only one bean can be created at a time.
  private final Set<Object> tempDependentBeans = new HashSet<>();
  private final Map<T, Set<Object>> dependentBeans = new HashMap<>();

  protected BeanFactory(BeanManager beanManager) {
    this.beanManager = (AbstractBeanManager) beanManager;
  }

  public T getIncompleteInstance() {
    return incompleteInstance;
  }

  protected void setIncompleteInstance(final T instance) {
    incompleteInstance = instance;
  }

  public abstract T getInstance();

  public void initInstance(T instance) {
    if (beanDef.getScope().equals(Dependent.class) || !initialized) {
      doInitInstance(instance);
      initialized = true;
    }
  }

  protected void doInitInstance(T instance) {

  }

  protected T createInstance() {
    throw new UnsupportedOperationException(
        "The factory, " + getClass().getSimpleName() + ", only supports contextual instances.");
  }

  protected T createInstanceInternal() {
    T instance = createInstance();
    return addBeanInstanceToPool(instance, this);
  }

  public T createContextualInstance(final Class<?>[] typeArgs, final Annotation[] qualifiers) {
    throw new UnsupportedOperationException(
        "The factory, " + getClass().getSimpleName() + ", does not support contextual instances.");
  }

  protected void onDestroy(T instance) {

  }

  void onDestroyInternal(T instance) {
    onDestroy(instance);
    if (dependentBeans.containsKey(instance)) {
      for (Object dependentBean : dependentBeans.get(instance)) {
        beanManager.destroyBean(dependentBean);
      }
    }
    dependentBeans.remove(instance);
    this.instance = null;
    this.initialized = false;
  }

  private T addBeanInstanceToPool(T instance, BeanFactory<T> factory) {
    return beanManager.addBeanInstanceToPool(instance, factory);
  }

  private final Predicate<InstanceFactory> isDependent = factory -> {
    if (factory instanceof SyncBeanDef) {
      SyncBeanDef<?> beanDef = (SyncBeanDef<?>) factory;
      return beanDef.getFactory().isPresent() && beanDef.getScope().equals(Dependent.class);
    }
    return false;
  };

  public <D> D addDependencyConstructor(InstanceFactory<D> factory, Set<Object> deps) {
    D instance = factory.getInstance();
    if (isDependent.test(factory)) {
      deps.add(instance);
    }
    return instance;
  }

  public void addDependencyConstructor(T instance, Set<Object> deps) {
    if (!dependentBeans.containsKey(instance)) {
      dependentBeans.put(instance, new HashSet<>());
    }
    dependentBeans.get(instance).addAll(deps);
  }

  public <D> D addDependencyField(T instance, InstanceFactory<D> factory) {
    if (!dependentBeans.containsKey(instance)) {
      dependentBeans.put(instance, new HashSet<>());
    }
    return addDependency(factory, dependentBeans.get(instance));
  }

  // TODO use disposable interface
  private <D> D addDependency(InstanceFactory<D> factory, Set<Object> holder) {
    D instance = factory.getInstance();
    if (isDependent.test(factory)) {
      holder.add(instance);
    }
    return instance;
  }

}
