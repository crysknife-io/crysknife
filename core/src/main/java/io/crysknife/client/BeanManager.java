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
package io.crysknife.client;

import io.crysknife.client.internal.BeanManagerUtil;
import io.crysknife.client.internal.QualifierUtil;
import io.crysknife.client.internal.SyncBeanDefImpl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/28/19
 */
public abstract class BeanManager {

  private final Map<Class, BeanDefinitionHolder> beans = new HashMap<>();

  BeanManager() {

  }

  void register(SyncBeanDefImpl beanDefinition) {
    BeanDefinitionHolder holder = get(beanDefinition.getType());
    holder.beanDefinition = beanDefinition;
    beanDefinition.getAssignableTypes().forEach(superType -> {
      get((Class<?>) superType).subTypes.add(holder);
    });
  }

  private BeanDefinitionHolder get(Class<?> type) {
    if (!beans.containsKey(type)) {
      BeanDefinitionHolder holder = new BeanDefinitionHolder();
      beans.put(type, holder);
    }
    return beans.get(type);
  }

  public <T> Collection<SyncBeanDef<T>> lookupBeans(final Class<T> type, Annotation... qualifiers) {
    List<Annotation> asList = Arrays.stream(qualifiers).collect(Collectors.toList());

    Set<SyncBeanDef<T>> result = new HashSet<>();
    if (beans.get(type).beanDefinition != null) {

      if (beans.get(type).beanDefinition
          .matches(Arrays.stream(qualifiers).collect(Collectors.toSet()))) {
        result.add(beans.get(type).beanDefinition);
      }
    }

    if (asList.isEmpty()) {
      beans.get(type).subTypes.stream().filter(f -> f.beanDefinition != null)
          .filter(f -> QualifierUtil.matches(asList,
              (Collection<Annotation>) f.beanDefinition.getQualifiers()))
          .forEach(bean -> result.add(bean.beanDefinition));
    } else {
      beans.get(type).subTypes.stream().filter(f -> f.beanDefinition != null)
          .filter(f -> QualifierUtil.matches(asList,
              (Collection<Annotation>) f.beanDefinition.getActualQualifiers()))
          .forEach(bean -> result.add(bean.beanDefinition));
    }
    return result;
  }

  public <T> SyncBeanDef<T> lookupBean(final Class<T> type) {
    return lookupBean(type, QualifierUtil.DEFAULT_ANNOTATION);
  }

  public <T> SyncBeanDef<T> lookupBean(final Class<T> type, final Annotation... qualifiers) {
    if (!beans.containsKey(type)) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, qualifiers);
    }

    List<Annotation> asList = Arrays.stream(qualifiers).collect(Collectors.toList());
    Collection<IOCBeanDef<T>> candidates = new HashSet<>();
    if (beans.get(type).beanDefinition != null) {
      SyncBeanDefImpl def = beans.get(type).beanDefinition;
      if (QualifierUtil.matches(asList, (Collection<Annotation>) def.getActualQualifiers())) {
        return def;
      }
      candidates.add(def);
    }

    beans.get(type).subTypes.stream().filter(bean -> bean.beanDefinition != null)
        .filter(f -> QualifierUtil.matches(asList,
            (Collection<Annotation>) f.beanDefinition.getActualQualifiers()))
        .forEach(bean -> candidates.add(bean.beanDefinition));

    if (candidates.size() > 1) {
      throw BeanManagerUtil.ambiguousResolutionException(type, candidates, qualifiers);
    } else if (candidates.isEmpty()) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, qualifiers);
    } else {
      return (SyncBeanDef<T>) candidates.iterator().next();
    }
  }

  public void destroyBean(Object ref) {
    // DO NOTHING ATM
  }


  private static class BeanDefinitionHolder {

    SyncBeanDefImpl beanDefinition;
    Set<BeanDefinitionHolder> subTypes = new HashSet<>();
  }
}

