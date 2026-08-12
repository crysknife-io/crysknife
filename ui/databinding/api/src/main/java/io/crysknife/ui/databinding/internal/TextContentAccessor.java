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
 * Accessor for generic {@link HTMLElement} — uses {@code textContent}. This accessor is one-way
 * (model → UI) since generic elements do not emit value change events.
 *
 * @author Dmitrii Tikhomirov
 */
public class TextContentAccessor implements ElementAccessor {

  public static final TextContentAccessor INSTANCE = new TextContentAccessor();

  @Override
  public Object getValue(HTMLElement element) {
    return element.textContent;
  }

  @Override
  public void setValue(HTMLElement element, Object value) {
    element.textContent = value == null ? "" : value.toString();
  }

  @Override
  public String getChangeEventType() {
    return null;
  }
}
