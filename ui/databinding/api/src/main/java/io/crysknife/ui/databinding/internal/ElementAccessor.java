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

import elemental2.dom.HTMLElement;

/**
 * Strategy for reading and writing values to elemental2 DOM elements. Implementations are selected
 * at compile time based on the element field type.
 *
 * @author Dmitrii Tikhomirov
 */
public interface ElementAccessor {

  Object getValue(HTMLElement element);

  void setValue(HTMLElement element, Object value);

  /**
   * Returns the DOM event type to listen for value changes (e.g. {@code "input"}, {@code "change"}),
   * or {@code null} for one-way (model → UI) binding only.
   */
  String getChangeEventType();
}
