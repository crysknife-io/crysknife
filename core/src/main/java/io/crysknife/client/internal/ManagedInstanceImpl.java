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

import io.crysknife.client.BeanManager;
import io.crysknife.client.ManagedInstance;
import io.crysknife.client.SyncBeanDef;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.Dependent;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/25/21
 */
public class ManagedInstanceImpl<T> implements ManagedInstance<T> {

  private final BeanManager beanManager;

  private final Class<T> type;

  private final Annotation[] qualifiers;

  private final Set<T> dependentInstances = new HashSet<>();

  public ManagedInstanceImpl(BeanManager beanManager, Class<T> type) {
    this(beanManager, type, new Annotation[] {});
  }

  public ManagedInstanceImpl(BeanManager beanManager, Class<T> type, Annotation... qualifiers) {
    this.type = type;
    this.beanManager = beanManager;
    this.qualifiers = qualifiers;
  }

  @Override
  public ManagedInstance<T> select(Annotation... annotations) {
    Annotation[] combined = Stream.concat(Arrays.stream(qualifiers), Arrays.stream(annotations))
        .toArray(Annotation[]::new);
    return new ManagedInstanceImpl<>(beanManager, type, combined);
  }

  @Override
  public <U extends T> ManagedInstance<U> select(Class<U> subtype, Annotation... annotations) {
    Annotation[] combined = Stream.concat(Arrays.stream(qualifiers), Arrays.stream(annotations))
        .toArray(Annotation[]::new);
    return new ManagedInstanceImpl<>(beanManager, subtype, combined);
  }

  @Override
  public boolean isUnsatisfied() {
    if (qualifiers == null || qualifiers.length == 0) {
      return beanManager.lookupBeans(type, QualifierUtil.DEFAULT_ANNOTATION).size() != 1;
    }
    return beanManager.lookupBeans(type, qualifiers).size() != 1;
  }

  @Override
  public boolean isAmbiguous() {
    return beanManager.lookupBeans(type, qualifiers).size() > 1;
  }

  @Override
  public void destroy(T instance) {
    if (beanManager.lookupBeanDefinition(instance).isPresent()) {
      if (beanManager.lookupBeanDefinition(instance).get().getScope().equals(Dependent.class)) {
        beanManager.destroyBean(instance);
        dependentInstances.remove(instance);
      }
    }
  }

  @Override
  public void destroyAll() {
    Set<T> removed = new HashSet<>();
    for (T instance : dependentInstances) {
      if (beanManager.lookupBeanDefinition(instance).isPresent()) {
        if (beanManager.lookupBeanDefinition(instance).get().getScope().equals(Dependent.class)) {
          beanManager.destroyBean(instance);
          removed.add(instance);
        }
      }
    }
    dependentInstances.removeAll(removed);
  }

  @Override
  @Nonnull
  public Iterator<T> iterator() {
    return new ManagedInstanceImplIterator(beanManager.lookupBeans(type, qualifiers));
  }

  @Override
  public T get() {
    if (qualifiers.length == 0) {
      return addDependentInstance(
          beanManager.lookupBean(type, QualifierUtil.DEFAULT_ANNOTATION).getInstance());
    }
    return addDependentInstance(beanManager.lookupBean(type, qualifiers).getInstance());
  }

  private T addDependentInstance(T instance) {
    dependentInstances.add(instance);
    return instance;
  }

  private class ManagedInstanceImplIterator implements Iterator<T> {

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
      return addDependentInstance(bean.getInstance());
    }
  }
}
