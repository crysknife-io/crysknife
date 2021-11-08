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

import io.crysknife.client.internal.IOCResolutionException;
import io.crysknife.client.internal.SyncBeanDefImpl;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/28/19
 */
public interface BeanManager {

  /**
   * Register a bean with the bean manager. The registered bean will be available for lookup, by
   * type and by name if applicable.
   *
   * @param beanDefinition The bean def to register.
   */
  void register(SyncBeanDefImpl beanDefinition);

  /**
   * Looks up all beans by name. The name is either the fully qualified type name of an assignable
   * type or a given name as specified by {@link javax.inject.Named}.
   *
   * @param name the fqcn of an assignable type, or a given name specified by
   *        {@link javax.inject.Named}, must not be null.
   * @return and unmodifiable list of all beans with the specified name.
   */
  @SuppressWarnings("rawtypes")
  Collection<SyncBeanDef> lookupBeans(String name);

  /**
   * Looks up a bean reference based on type and qualifiers.s
   *
   * @param type The type of the bean
   * @param qualifiers qualifiers to match
   * @return An unmodifiable list of all beans which match the specified type and qualifiers.
   *         Returns an empty list if no beans match.
   */
  <T> Collection<SyncBeanDef<T>> lookupBeans(final Class<T> type, Annotation... qualifiers);

  /**
   * Looks up a bean reference based on type and qualifiers.
   *
   * @param type The type of the bean
   * @param <T> The type of the bean
   * @return An instance of the {@link SyncBeanDef} for the matching type and qualifiers. Throws an
   *         {@link IOCResolutionException} if there is a matching type but none of the qualifiers
   *         match or if more than one bean matches.
   */
  <T> SyncBeanDef<T> lookupBean(final Class<T> type);

  /**
   * Looks up a bean reference based on type and qualifiers.
   *
   * @param type The type of the bean
   * @param qualifiers qualifiers to match
   * @param <T> The type of the bean
   * @return An instance of the {@link SyncBeanDef} for the matching type and qualifiers. Throws an
   *         {@link IOCResolutionException} if there is a matching type but none of the qualifiers
   *         match or if more than one bean matches.
   */
  <T> SyncBeanDef<T> lookupBean(final Class<T> type, Annotation... qualifiers);

  /**
   * Destroy a bean and all other dependent scoped dependencies of this bean in the bean manager.
   *
   * @param ref The instance reference of the bean.
   */
  void destroyBean(Object ref);
}

