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

import io.crysknife.client.SyncBeanDef;

import javax.enterprise.context.Dependent;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/25/21
 */
public class SyncBeanDefImpl<T> implements SyncBeanDef<T> {

  private static final List<Annotation> defaultQualifiers =
      Arrays.asList(QualifierUtil.DEFAULT_QUALIFIERS);

  private final Class<T> actualType;
  private final Class<? extends Annotation> scope;
  private List<Annotation> qualifiers;
  private List<Class<?>> assignableTypes;
  private Optional<BeanFactory<T>> factory = Optional.empty();

  protected SyncBeanDefImpl(final Class<T> actualType) {
    this.actualType = actualType;
    this.scope = Dependent.class;
  }

  protected SyncBeanDefImpl(final Class<T> actualType, final Class<? extends Annotation> scope) {
    this.actualType = actualType;
    this.scope = scope;
  }

  @Override
  public boolean isAssignableTo(Class<?> type) {
    return getAssignableTypes().contains(type);
  }

  public Collection<Class<?>> getAssignableTypes() {
    return Collections.unmodifiableCollection(assignableTypes);
  }

  @Override
  public Class<T> getType() {
    return actualType;
  }

  @Override
  public Class<?> getBeanClass() {
    return actualType;
  }

  public Class<? extends Annotation> getScope() {
    return scope;
  }

  @Override
  public Collection<Annotation> getQualifiers() {
    Set<Annotation> temp = new HashSet<>(defaultQualifiers);
    if (qualifiers != null)
      temp.addAll(qualifiers);
    return Collections.unmodifiableCollection(temp);
  }

  @Override
  public Collection<Annotation> getActualQualifiers() {
    if (qualifiers == null) {
      return Collections.emptySet();
    } else {
      return Collections.unmodifiableCollection(qualifiers);
    }
  }

  @Override
  public Optional<BeanFactory<T>> getFactory() {
    return factory;
  }

  @Override
  public boolean matches(Set<Annotation> annotations) {
    return QualifierUtil.matches(annotations, getQualifiers());
  }

  @Override
  public String getName() {
    return actualType.getCanonicalName();
  }

  @Override
  public String toString() {
    String qualifiers = "";
    if (this.qualifiers != null) {
      qualifiers = BeanManagerUtil
          .qualifiersToString(this.qualifiers.toArray(new Annotation[this.qualifiers.size()]));
    }

    return "[type=" + actualType + ", scope=" + scope.getSimpleName() + ", qualifiers=" + qualifiers
        + "]";
  }

  @Override
  public T getInstance() {
    if (!factory.isPresent()) {
      BeanManagerUtil.noFactoryResolutionException(actualType,
          qualifiers.toArray(new Annotation[qualifiers.size()]));
    }
    return factory.get().getInstance();
  }

  @Override
  public T newInstance() {
    if (!factory.isPresent()) {
      BeanManagerUtil.noFactoryResolutionException(actualType,
          qualifiers.toArray(new Annotation[qualifiers.size()]));
    }
    return factory.get().createInstance();
  }

  public static class Builder {

    private final Class<?> actualType;
    private final Class<? extends Annotation> scope;
    private List<Annotation> qualifiers;
    private List<Class<?>> assignableTypes;
    private BeanFactory factory;

    public Builder(final Class<?> actualType, final Class<? extends Annotation> scope) {
      this.actualType = actualType;
      this.scope = scope;
    }

    public Builder withQualifiers(final Annotation[] qualifiers) {
      this.qualifiers = Arrays.asList(qualifiers);
      return this;
    }

    public Builder withAssignableTypes(final Class<?>[] assignableTypes) {
      this.assignableTypes = Arrays.asList(assignableTypes);
      return this;
    }

    public Builder withFactory(BeanFactory factory) {
      this.factory = factory;
      return this;
    }

    public SyncBeanDefImpl build() {
      SyncBeanDefImpl definition = new SyncBeanDefImpl(actualType, scope);
      if (qualifiers != null) {
        definition.qualifiers = qualifiers;
      }

      if (assignableTypes != null) {
        definition.assignableTypes = assignableTypes;
      }

      if (factory != null) {
        factory.beanDef = definition;
        definition.factory = Optional.of(factory);
      }

      return definition;
    }
  }
}
