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

package io.crysknife.ui.navigation.client.local.spi;

import io.crysknife.client.ioc.ContextualTypeProvider;
import io.crysknife.client.ioc.IOCProvider;
import io.crysknife.ui.navigation.client.local.Navigation;
import io.crysknife.ui.navigation.client.local.TransitionTo;
import jakarta.inject.Inject;

import java.lang.annotation.Annotation;

@IOCProvider
public class TransitionToContextualTypeProvider implements ContextualTypeProvider<TransitionTo> {

  @Inject
  private Navigation navigation;

  @Override
  public TransitionTo provide(Class<?>[] typeargs, Annotation[] qualifiers) {
    Class clazz = typeargs[0];
    if (typeargs.length == 0) {
      throw new IllegalArgumentException("TransitionTo must be parameterized with a page type");
    }
    return new TransitionTo(clazz, navigation);
  }
}
