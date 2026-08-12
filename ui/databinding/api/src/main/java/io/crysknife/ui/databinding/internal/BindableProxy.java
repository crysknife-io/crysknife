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

/**
 * Interface implemented by generated proxy subclasses of {@code @Bindable} model classes. The proxy
 * delegates all getter/setter calls to an internal target and notifies the {@link BindingAgent} on
 * property changes.
 *
 * @param <T> the model type
 * @author Dmitrii Tikhomirov
 */
public interface BindableProxy<T> {

  void setTarget(T target);

  T unwrap();

  BindingAgent getBindingAgent();

  Object getPropertyValue(String propertyName);

  void setPropertyValue(String propertyName, Object value);
}
