/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.treblereel.injection.inheritance.factories;

import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/24/21
 */
public abstract class AbstractTypeDefinitionFactory<T> implements TypeDefinitionFactory<T> {

  @Override
  public boolean accepts(final Class<? extends T> type) {
    return getAcceptedClasses().contains(type);
  }

  public abstract Set<Class<? extends T>> getAcceptedClasses();

  @Override
  public boolean accepts(final String id) {
    return getClass(id) != null;
  }

  @Override
  public T build(final String id) {
    final Class<? extends T> clazz = getClass(id);
    if (null != clazz) {
      return build(clazz);
    }
    return null;
  }

  protected Class<? extends T> getClass(final String id) {
    return null;
  }
}
