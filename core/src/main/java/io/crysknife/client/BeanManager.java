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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    Set<SyncBeanDef<T>> result = new HashSet<>();
    if (!beans.containsKey(type)) {
      return result;
    }

    if (qualifiers.length == 0) {
      if (beans.get(type).beanDefinition != null) {
        result.add(beans.get(type).beanDefinition);
      }
      beans.get(type).subTypes.stream().filter(f -> f.beanDefinition != null)
          .forEach(bean -> result.add(bean.beanDefinition));
      return result;
    }

    if (beans.get(type).beanDefinition != null) {
      if (compareAnnotations(beans.get(type).beanDefinition.getActualQualifiers(), qualifiers)) {
        result.add(beans.get(type).beanDefinition);
      }
    }
    beans.get(type).subTypes.stream().filter(f -> f.beanDefinition != null)
        .filter(f -> compareAnnotations(f.beanDefinition.getActualQualifiers(), qualifiers))
        .forEach(bean -> result.add(bean.beanDefinition));

    return result;
  }

  private boolean compareAnnotations(Collection<Annotation> all, Annotation... in) {
    Annotation[] _all = all.toArray(new Annotation[all.size()]);
    return QualifierUtil.matches(in, _all);
  }

  public <T> SyncBeanDef<T> lookupBean(final Class<T> type) {
    return lookupBean(type, QualifierUtil.DEFAULT_ANNOTATION);
  }

  public <T> SyncBeanDef<T> lookupBean(final Class<T> type, Annotation... qualifiers) {
    if (!beans.containsKey(type)) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, qualifiers);
    }
    if (qualifiers == null || qualifiers.length == 0) {
      qualifiers = new Annotation[] {QualifierUtil.DEFAULT_ANNOTATION};
    }

    Collection<IOCBeanDef<T>> candidates = new HashSet<>();
    if (beans.get(type).beanDefinition != null) {
      if (compareAnnotations(beans.get(type).beanDefinition.getQualifiers(), qualifiers)) {
        candidates.add(beans.get(type).beanDefinition);
      }
    }
    Annotation[] a1 = new Annotation[] {qualifiers[0]};
    Annotation[] a2 = new Annotation[] {QualifierUtil.DEFAULT_ANNOTATION};

    if (qualifiers.length == 1 && !beans.get(type).subTypes.isEmpty()
        && compareAnnotations(a1, a2)) {
      for (BeanDefinitionHolder subType : beans.get(type).subTypes) {
        if (subType.beanDefinition != null) {
          if (!subType.beanDefinition.getActualQualifiers().isEmpty() && compareAnnotations(
              subType.beanDefinition.getActualQualifiers(), QualifierUtil.SPECIALIZES_ANNOTATION)) {
            return subType.beanDefinition;
          }
        }
      }
      for (BeanDefinitionHolder subType : beans.get(type).subTypes) {
        if (subType.beanDefinition != null) {
          if (!subType.beanDefinition.getActualQualifiers().isEmpty() && compareAnnotations(
              subType.beanDefinition.getActualQualifiers(), QualifierUtil.DEFAULT_ANNOTATION)) {
            return subType.beanDefinition;
          }
        }
      }
      for (BeanDefinitionHolder subType : beans.get(type).subTypes) {
        Set<Annotation> annotations = new HashSet<>();
        annotations.add(QualifierUtil.DEFAULT_ANNOTATION);
        if (compareAnnotations(subType.beanDefinition.getActualQualifiers(), qualifiers)) {
          candidates.add(subType.beanDefinition);
        }
      }
    } else {
      Set<Annotation> _qual = new HashSet<>();
      Collections.addAll(_qual, qualifiers);
      Collections.addAll(_qual, QualifierUtil.DEFAULT_QUALIFIERS);
      _qual.toArray(new Annotation[_qual.size()]);

      for (BeanDefinitionHolder subType : beans.get(type).subTypes) {
        if (compareAnnotations(subType.beanDefinition.getQualifiers(),
            _qual.toArray(new Annotation[_qual.size()]))) {
          candidates.add(subType.beanDefinition);
        }
      }
    }

    if (candidates.size() > 1) {
      throw BeanManagerUtil.ambiguousResolutionException(type, candidates, qualifiers);
    } else if (candidates.isEmpty()) {
      throw BeanManagerUtil.unsatisfiedResolutionException(type, qualifiers);
    } else {
      return (SyncBeanDef<T>) candidates.iterator().next();
    }
  }

  private boolean compareAnnotations(Annotation[] all, Annotation[] in) {
    return QualifierUtil.matches(in, all);
  }

  public void destroyBean(Object ref) {
    // DO NOTHING ATM
  }


  private static class BeanDefinitionHolder {

    SyncBeanDefImpl beanDefinition;
    Set<BeanDefinitionHolder> subTypes = new HashSet<>();
  }
}

