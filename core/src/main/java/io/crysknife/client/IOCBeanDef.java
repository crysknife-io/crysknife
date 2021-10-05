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

package io.crysknife.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

/**
 * Definition of a managed bean.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface IOCBeanDef<T> {

  /**
   * @param type Must not be null.
   * @return True if this bean is assginable to the given type.
   */
  boolean isAssignableTo(Class<?> type);

  /**
   * Returns the type of the bean.
   *
   * @see #getBeanClass()
   * @return the type of the bean.
   */
  Class<T> getType();

  /**
   * Returns the actual bean class represented by this bean.
   *
   * @return the actual type of the bean.
   */
  Class<?> getBeanClass();

  /**
   * Returns the scope of the bean.
   *
   * @returns the annotation type representing the scope of the bean.
   */
  Class<? extends Annotation> getScope();

  /**
   * Returns any qualifiers associated with the bean.
   *
   * @return Must never be null.
   */
  Collection<Annotation> getQualifiers();

  Collection<Annotation> getActualQualifiers();

  /**
   * Returns true if the beans qualifiers match the specified set of qualifiers.
   *
   * @param annotations the qualifiers to compare
   * @return returns whether or not the bean matches the set of qualifiers
   */
  boolean matches(Set<Annotation> annotations);

  /**
   * Returns the name of the bean.
   *
   * @return the name of the bean. If the bean does not have a name, returns null.
   */
  String getName();

}
