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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Named;
import javax.inject.Provider;

import io.crysknife.client.BeanManager;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/29/19
 */
public abstract class AbstractBeanManager implements BeanManager {

  final private Map<Class, Map<InstanceKey, Provider>> beanStore = new java.util.HashMap<>();

  protected AbstractBeanManager() {

  }

  @Override
  public void destroyBean(Object ref) {

  }

  @Override
  public <T> Instance<T> lookupBean(Class type, Annotation... qualifiers) {
    if (beanStore.get(type) != null) {
      InstanceKey instanceKey = new InstanceKey(type, qualifiers);
      if (beanStore.get(type).containsKey(instanceKey)) {
        return new InstanceImpl<T>(beanStore.get(type).get(instanceKey));
      }
    }
    throw new Error("Unable to find the bean [" + type.getCanonicalName()
        + "] with the qualifiers [" + Arrays.stream(qualifiers)
            .map(q -> q.annotationType().getCanonicalName()).collect(Collectors.joining(","))
        + "]");
  }

  @Override
  public <T> Instance<T> lookupBean(Class type) {
    return lookupBean(type, new Default() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Default.class;
      }
    });
  }

  @Override
  public <T> Set<Instance<T>> lookupBeans(Class type, Annotation... qualifiers) {
    if (beanStore.get(type) != null) {
      final Set<Instance<T>> beans = new HashSet<>();
      if (qualifiers.length == 0) {
        beanStore.get(type).values().stream()
            .map((Function<Provider, InstanceImpl>) InstanceImpl::new)
            .forEach(bean -> beans.add(bean));
      } else {
        InstanceKey key = new InstanceKey(type, qualifiers);

        beanStore.get(type).entrySet().stream().filter(pre -> pre.getKey().equals(key))
            .map(provider -> new InstanceImpl<>(provider.getValue()))
            .forEach(bean -> beans.add((InstanceImpl<T>) bean));
      }

      return Collections.unmodifiableSet(beans);
    }

    throw new Error("Unable to find the bean [" + type.getCanonicalName()
        + "] with the qualifiers [" + Arrays.stream(qualifiers)
            .map(elm -> elm.annotationType().getCanonicalName()).collect(Collectors.joining(","))
        + " ]");
  }

  protected void register(Class type, Provider provider) {
    register(type, provider, new Default() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Default.class;
      }
    });
  }

  protected void register(Class type, Provider provider, Annotation... annotation) {
    if (!beanStore.containsKey(type)) {
      beanStore.put(type, new HashMap<>());
    }
    beanStore.get(type).put(new InstanceKey(type, annotation), provider);
  }

  private static class InstanceKey {

    private final Class type;
    private final Set<Key> qualifiers;

    private InstanceKey(final Class type, final Annotation... qualifiers) {
      this.type = type;
      this.qualifiers = Arrays.stream(qualifiers).map(q -> {
        Key key;
        if (q instanceof Named) {
          key = new Key(q.annotationType(), ((Named) q).value());
        } else {
          key = new Key(q.annotationType());
        }
        return key;
      }).collect(Collectors.toSet());
    }

    @Override
    public int hashCode() {

      return Arrays.hashCode(qualifiers.toArray());
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof InstanceKey) {
        final InstanceKey other = (InstanceKey) obj;
        return type.equals(other.type) && qualifiers.equals(other.qualifiers);
      } else {
        return false;
      }
    }

    private static class Key {

      private Class<? extends Annotation> annotation;
      private String named;

      private Key(Class<? extends Annotation> annotation, String named) {
        this(annotation);
        this.named = named;
      }

      private Key(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
      }

      @Override
      public int hashCode() {
        return Objects.hash(annotation, named);
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }
        Key key = (Key) o;
        return annotation.equals(key.annotation) && Objects.equals(named, key.named);
      }
    }
  }
}
