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

import io.crysknife.client.BeanManager;
import io.crysknife.client.IOCBeanDef;
import io.crysknife.client.SyncBeanDef;

import javax.enterprise.inject.Typed;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/28/19
 */
public abstract class AbstractBeanManager implements BeanManager {

  private final Map<Class, BeanDefinitionHolder> beans = new HashMap<>();
  private final Map<String, Class> beansByBeanName = new HashMap<>();

  protected AbstractBeanManager() {

  }

  public void register(SyncBeanDefImpl beanDefinition) {
    BeanDefinitionHolder holder = get(beanDefinition.getType());
    holder.beanDefinition = beanDefinition;
    beanDefinition.getAssignableTypes().forEach(superType -> {
      get((Class<?>) superType).subTypes.add(holder);
      beansByBeanName.put(((Class<?>) superType).getCanonicalName(), (Class<?>) superType);

    });
    beansByBeanName.put(beanDefinition.getName(), beanDefinition.getType());
  }

  private BeanDefinitionHolder get(Class<?> type) {
    if (!beans.containsKey(type)) {
      BeanDefinitionHolder holder = new BeanDefinitionHolder();
      beans.put(type, holder);
    }
    return beans.get(type);
  }

  @Override
  public Collection<SyncBeanDef> lookupBeans(String name) {
    if (beansByBeanName.containsKey(name)) {
      return lookupBeans(beansByBeanName.get(name));
    }


    return Collections.EMPTY_SET;
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
    Collection<IOCBeanDef<T>> candidates = doLookupBean(type, qualifiers);

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

  <T> Collection<IOCBeanDef<T>> doLookupBean(final Class<T> type, Annotation... qualifiers) {
    Collection<IOCBeanDef<T>> candidates = new HashSet<>();
    if (beans.containsKey(type)) {

      if (qualifiers == null || qualifiers.length == 0) {
        qualifiers = new Annotation[] {QualifierUtil.DEFAULT_ANNOTATION};
      }

      if (beans.get(type).beanDefinition != null) {
        if (beans.get(type).beanDefinition.getTyped().isPresent())
          if (Arrays.stream(((Typed) beans.get(type).beanDefinition.getTyped().get()).value())
              .anyMatch(any -> any.equals(type))) {
            Set<IOCBeanDef<T>> result = new HashSet<>();
            result.add(beans.get(type).beanDefinition);
            return result;
          }

        if (compareAnnotations(beans.get(type).beanDefinition.getQualifiers(), qualifiers)) {
          if (beans.get(type).beanDefinition.getFactory().isPresent()) {
            candidates.add(beans.get(type).beanDefinition);
          }
        }
      }

      if (qualifiers.length == 1 && !beans.get(type).subTypes.isEmpty() && isDefault(qualifiers)) {
        for (BeanDefinitionHolder subType : beans.get(type).subTypes) {
          if (subType.beanDefinition != null) {
            if (!subType.beanDefinition.getActualQualifiers().isEmpty()
                && compareAnnotations(subType.beanDefinition.getActualQualifiers(),
                    QualifierUtil.SPECIALIZES_ANNOTATION)) {
              Collection<IOCBeanDef<T>> result = new HashSet<>();
              result.add(subType.beanDefinition);
              return result;
            }
          }
        }
        for (BeanDefinitionHolder subType : beans.get(type).subTypes) {
          if (subType.beanDefinition != null) {
            if (!subType.beanDefinition.getActualQualifiers().isEmpty() && compareAnnotations(
                subType.beanDefinition.getActualQualifiers(), QualifierUtil.DEFAULT_ANNOTATION)) {
              Collection<IOCBeanDef<T>> result = new HashSet<>();
              result.add(subType.beanDefinition);
              return result;
            }
          }
        }
        for (BeanDefinitionHolder subType : beans.get(type).subTypes) {
          Set<Annotation> annotations = new HashSet<>();
          annotations.add(QualifierUtil.DEFAULT_ANNOTATION);
          if (compareAnnotations(subType.beanDefinition.getActualQualifiers(), qualifiers)) {
            if (subType.beanDefinition.getTyped().isPresent()) {
              continue;
            } else if (subType.beanDefinition.getFactory().isPresent())
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
            if (subType.beanDefinition.getTyped().isPresent()) {
              continue;
            } else if (subType.beanDefinition.getFactory().isPresent())
              candidates.add(subType.beanDefinition);
          }
        }
      }
    }
    return candidates;
  }

  private boolean isDefault(Annotation[] qualifiers) {
    Annotation[] a1 = new Annotation[] {qualifiers[0]};
    Annotation[] a2 = new Annotation[] {QualifierUtil.DEFAULT_ANNOTATION};
    return QualifierUtil.matches(a1, a2);
  }

  private boolean compareAnnotations(Annotation[] all, Annotation[] in) {
    return QualifierUtil.matches(in, all);
  }

  private static class BeanDefinitionHolder {

    SyncBeanDefImpl beanDefinition;
    Set<BeanDefinitionHolder> subTypes = new HashSet<>();
  }
}

