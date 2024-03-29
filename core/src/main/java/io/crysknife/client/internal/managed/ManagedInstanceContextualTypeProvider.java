/*
 * Copyright © 2023 Treblereel
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

package io.crysknife.client.internal.managed;

import java.lang.annotation.Annotation;

import jakarta.inject.Inject;

import io.crysknife.client.BeanManager;
import io.crysknife.client.ManagedInstance;
import io.crysknife.client.internal.ManagedInstanceImpl;
import io.crysknife.client.ioc.ContextualTypeProvider;
import io.crysknife.client.ioc.IOCProvider;

@IOCProvider
public class ManagedInstanceContextualTypeProvider
    implements ContextualTypeProvider<ManagedInstance> {

  private final BeanManager manager;

  @Inject
  public ManagedInstanceContextualTypeProvider(BeanManager manager) {
    this.manager = manager;
  }

  @Override
  public ManagedInstance provide(Class<?>[] typeargs, Annotation[] qualifiers) {
    Class clazz = typeargs[0];
    return new ManagedInstanceImpl(manager, clazz, qualifiers);
  }
}
