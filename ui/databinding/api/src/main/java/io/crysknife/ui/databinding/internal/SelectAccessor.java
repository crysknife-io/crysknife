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
import elemental2.dom.HTMLSelectElement;
import jsinterop.base.Js;

/**
 * Accessor for {@link HTMLSelectElement}.
 *
 * @author Dmitrii Tikhomirov
 */
public class SelectAccessor implements ElementAccessor {

  public static final SelectAccessor INSTANCE = new SelectAccessor();

  @Override
  public Object getValue(HTMLElement element) {
    return Js.<HTMLSelectElement>uncheckedCast(element).value;
  }

  @Override
  public void setValue(HTMLElement element, Object value) {
    Js.<HTMLSelectElement>uncheckedCast(element).value = value == null ? "" : value.toString();
  }

  @Override
  public String getChangeEventType() {
    return "change";
  }
}
