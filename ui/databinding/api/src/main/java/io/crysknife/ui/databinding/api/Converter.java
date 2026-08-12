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
package io.crysknife.ui.databinding.api;

/**
 * Converts values between a model property type and a UI element value type.
 *
 * @param <M> model property type
 * @param <W> UI element (widget) value type
 * @author Dmitrii Tikhomirov
 */
public interface Converter<M, W> {

  /**
   * Converts a UI element value to the model property type.
   */
  M toModelValue(W widgetValue);

  /**
   * Converts a model property value to the UI element value type.
   */
  W toWidgetValue(M modelValue);
}
