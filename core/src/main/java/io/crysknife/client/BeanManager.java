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

import javax.enterprise.inject.Instance;
import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/28/19
 */
public interface BeanManager {

  void destroyBean(Object ref);

  <T> Instance<T> lookupBean(final Class type, Annotation... qualifiers);

  <T> Instance<T> lookupBean(final Class type);

  <T> Iterable<Instance<T>> lookupBeans(final Class type, Annotation... qualifiers);
}
