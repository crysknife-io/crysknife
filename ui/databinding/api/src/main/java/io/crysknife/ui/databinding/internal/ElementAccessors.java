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
 * Selects the appropriate {@link ElementAccessor} for a given DOM element at runtime, based on its
 * tag name and attributes.
 *
 * @author Dmitrii Tikhomirov
 */
public final class ElementAccessors {

  private ElementAccessors() {
  }

  public static ElementAccessor forElement(HTMLElement element) {
    String tagName = element.tagName.toUpperCase();
    switch (tagName) {
      case "INPUT":
        String type = element.getAttribute("type");
        if ("checkbox".equalsIgnoreCase(type) || "radio".equalsIgnoreCase(type)) {
          return CheckboxAccessor.INSTANCE;
        }
        return InputTextAccessor.INSTANCE;
      case "SELECT":
        return SelectAccessor.INSTANCE;
      case "TEXTAREA":
        return TextAreaAccessor.INSTANCE;
      default:
        return TextContentAccessor.INSTANCE;
    }
  }
}
