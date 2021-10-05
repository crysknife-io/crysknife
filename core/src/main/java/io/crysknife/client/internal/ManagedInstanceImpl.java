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

import io.crysknife.client.BeanManager;
import io.crysknife.client.ManagedInstance;
import io.crysknife.client.SyncBeanDef;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/25/21
 */
public class ManagedInstanceImpl<T> implements ManagedInstance<T> {

  private final BeanManager beanManager;

  private final Class<T> type;

  private final Collection<SyncBeanDef<T>> beans;

  private Annotation[] qualifiers;

  public ManagedInstanceImpl(BeanManager beanManager, Class<T> type) {
    this(beanManager, type, new Annotation[] {});
  }

  public ManagedInstanceImpl(BeanManager beanManager, Class<T> type, Annotation... qualifiers) {
    this.type = type;
    this.beanManager = beanManager;
    this.qualifiers = qualifiers;
    this.beans = beanManager.lookupBeans(type);
  }

  public ManagedInstanceImpl(Class<T> type, BeanManager beanManager,
      Collection<SyncBeanDef<T>> beans) {
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
  public void destroy(T instance) {

  }

  @Override
  public void destroyAll() {

  }

  @Override
  public Iterator<T> iterator() {
    return new ManagedInstanceImplIterator<T>(beans);
  }

  @Override
  public T get() {
    return beanManager.<T>lookupBean(type, qualifiers).getInstance();
  }

  private static class ManagedInstanceImplIterator<T> implements Iterator<T> {

    private final Iterator<SyncBeanDef<T>> delegate;

    public ManagedInstanceImplIterator(final Collection<SyncBeanDef<T>> beans) {
      this.delegate = beans.iterator();
    }

    @Override
    public boolean hasNext() {
      return delegate.hasNext();
    }

    @Override
    public T next() {
      final SyncBeanDef<T> bean = delegate.next();
      final T instance = bean.getInstance();
      return instance;
    }
  }
}
