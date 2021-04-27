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

package io.crysknife.client.internal;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import io.crysknife.client.BeanManager;
import io.crysknife.client.Instance;
import io.crysknife.client.ManagedInstance;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/25/21
 */
public class ManagedInstanceImpl<T> implements ManagedInstance<T> {

  private final BeanManager beanManager;

  private final Class<T> type;

  private final Set<Instance<T>> beans;

  public ManagedInstanceImpl(Class<T> type, BeanManager beanManager) {
    this.type = type;
    this.beanManager = beanManager;
    this.beans = beanManager.lookupBeans(type);
  }

  public ManagedInstanceImpl(Class<T> type, BeanManager beanManager, Set<Instance<T>> beans) {
    this.type = type;
    this.beanManager = beanManager;
    this.beans = beans;
  }

  @Override
  public ManagedInstance<T> select(Annotation... qualifiers) {
    return new ManagedInstanceImpl<>(type, beanManager, beanManager.lookupBeans(type, qualifiers));
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
  public void destroy(Object instance) {

  }

  @Override
  public void destroyAll() {

  }

  @Override
  public Iterator<T> iterator() {
    return new ManagedInstanceImplIterator(beans);
  }

  @Override
  public T get() {
    return beanManager.lookupBean(type).get();
  }

  private static class ManagedInstanceImplIterator<T> implements Iterator<T> {

    private final Iterator<Instance<T>> delegate;

    public ManagedInstanceImplIterator(final Collection<Instance<T>> beans) {
      this.delegate = beans.iterator();
    }

    @Override
    public boolean hasNext() {
      return delegate.hasNext();
    }

    @Override
    public T next() {
      final Instance<T> bean = delegate.next();
      final T instance = bean.get();
      return instance;
    }
  }
}
