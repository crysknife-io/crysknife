/*
 * Copyright © 2025 Treblereel
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
package io.crysknife.ui.databinding.provider;

import java.lang.annotation.Annotation;

import jakarta.inject.Inject;

import io.crysknife.client.BeanManager;
import io.crysknife.client.ioc.ContextualTypeProvider;
import io.crysknife.client.ioc.IOCProvider;
import io.crysknife.ui.databinding.api.DataBinder;

/**
 * CDI provider that handles {@code @Inject DataBinder<T>} injection points. Delegates to
 * {@link DataBinder#forType(Class)} using the resolved type argument.
 *
 * @author Dmitrii Tikhomirov
 */
@IOCProvider
public class DataBinderContextualTypeProvider implements ContextualTypeProvider<DataBinder<?>> {

  @Inject
  public DataBinderContextualTypeProvider(BeanManager manager) {
  }

  @SuppressWarnings("unchecked")
  @Override
  public DataBinder provide(Class<?>[] typeargs, Annotation[] qualifiers) {
    return DataBinder.forType(typeargs[0]);
  }
}
