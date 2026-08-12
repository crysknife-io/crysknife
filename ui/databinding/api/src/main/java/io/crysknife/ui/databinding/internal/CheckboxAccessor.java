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
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;

/**
 * Accessor for {@link HTMLInputElement} with type {@code checkbox} or {@code radio}.
 *
 * @author Dmitrii Tikhomirov
 */
public class CheckboxAccessor implements ElementAccessor {

  public static final CheckboxAccessor INSTANCE = new CheckboxAccessor();

  @Override
  public Object getValue(HTMLElement element) {
    return Js.<HTMLInputElement>uncheckedCast(element).checked;
  }

  @Override
  public void setValue(HTMLElement element, Object value) {
    Js.<HTMLInputElement>uncheckedCast(element).checked =
        value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(String.valueOf(value));
  }

  @Override
  public String getChangeEventType() {
    return "change";
  }
}
