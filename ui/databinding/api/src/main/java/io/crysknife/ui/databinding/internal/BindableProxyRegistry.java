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
package io.crysknife.ui.databinding.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Static registry mapping model classes to their generated proxy factory functions. Populated at
 * bootstrap time by generated code.
 *
 * @author Dmitrii Tikhomirov
 */
public final class BindableProxyRegistry {

  private static final Map<Class<?>, Supplier<BindableProxy<?>>> factories = new HashMap<>();

  private BindableProxyRegistry() {
  }

  public static void register(Class<?> modelType, Supplier<BindableProxy<?>> factory) {
    factories.put(modelType, factory);
  }

  public static BindableProxy<?> create(Class<?> modelType) {
    Supplier<BindableProxy<?>> factory = factories.get(modelType);
    if (factory == null) {
      throw new IllegalArgumentException(
          "No @Bindable proxy registered for " + modelType.getName()
              + ". Annotate the class with @Bindable.");
    }
    return factory.get();
  }
}
