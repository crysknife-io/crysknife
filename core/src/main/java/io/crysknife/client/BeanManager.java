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

  public <T> Collection<SyncBeanDef<T>> lookupBeans(final Class<T> type) {
    return lookupBeans(type, QualifierUtil.DEFAULT_ANNOTATION);
  }

  public <T> Collection<SyncBeanDef<T>> lookupBeans(final Class<T> type, Annotation... qualifiers) {
    if (Arrays.stream(qualifiers).count() == 0) {
      qualifiers = new Annotation[] {QualifierUtil.DEFAULT_ANNOTATION};
    }

    System.out.println("lookupBeans " + type + " " + Arrays.stream(qualifiers).count());

    for (Annotation qualifier : qualifiers) {
      System.out.println("qualifier " + qualifier.annotationType().getCanonicalName());
    }

    Set<SyncBeanDef<T>> result = new HashSet<>();
    if (beans.get(type).beanDefinition != null) {
      System.out.println("!= null ");

      for (Annotation qualifier : (Collection<Annotation>) beans.get(type).beanDefinition
          .getQualifiers()) {
        System.out.println(" in " + qualifier.annotationType().getCanonicalName());

      }

      if (beans.get(type).beanDefinition
          .matches(Arrays.stream(qualifiers).collect(Collectors.toSet()))) {
        result.add(beans.get(type).beanDefinition);
      }
    }
    List<Annotation> asList = Arrays.stream(qualifiers).collect(Collectors.toList());
    beans.get(type).subTypes.stream().filter(f -> f.beanDefinition != null)
        .filter(f -> QualifierUtil.matches(asList,
            (Collection<Annotation>) f.beanDefinition.getQualifiers()))
        .forEach(bean -> result.add(bean.beanDefinition));

    return result;
  }

  public <T> SyncBeanDef<T> lookupBean(final Class<T> type) {
    return lookupBean(type, QualifierUtil.DEFAULT_ANNOTATION);
  }

  public <T> SyncBeanDef<T> lookupBean(final Class<T> type, final Annotation... qualifiers) {

    System.out.println("lookupBean " + type + " " + BeanManagerUtil.qualifiersToString(qualifiers));

    if (!beans.containsKey(type)) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, qualifiers);
    }

    Collection<IOCBeanDef<T>> candidates = new HashSet<>();
    if (beans.get(type).beanDefinition != null) {
      candidates.add(beans.get(type).beanDefinition);
    }

    List<Annotation> asList = Arrays.stream(qualifiers).collect(Collectors.toList());


    beans.get(type).subTypes.stream().filter(bean -> bean.beanDefinition != null)
        .filter(f -> QualifierUtil.matches(asList,
            (Collection<Annotation>) f.beanDefinition.getActualQualifiers()))
        .forEach(bean -> candidates.add(bean.beanDefinition));



    for (IOCBeanDef<T> candidate : candidates) {
      System.out.println("candidate " + candidate.getType());
    }

    if (candidates.size() > 1) {
      throw BeanManagerUtil.ambiguousResolutionException(type, candidates, qualifiers);
    } else if (candidates.isEmpty()) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, qualifiers);
    } else {
      return (SyncBeanDef<T>) candidates.iterator().next();
    }
  }

  private static class BeanDefinitionHolder {

    SyncBeanDefImpl beanDefinition;
    Set<BeanDefinitionHolder> subTypes = new HashSet<>();
  }
}

