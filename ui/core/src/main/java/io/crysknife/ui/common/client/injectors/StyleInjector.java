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

package io.crysknife.ui.common.client.injectors;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLStyleElement;

public class StyleInjector {

  private final String styleBody;

  public static StyleInjector fromString(String contents) {
    return new StyleInjector(contents);
  }

  private StyleInjector(String styleBody) {
    this.styleBody = styleBody;
  }

  private HTMLStyleElement createElement(String contents) {
    HTMLStyleElement style = (HTMLStyleElement) DomGlobal.document.createElement("style");
    style.setAttribute("language", "text/css");
    style.innerHTML = contents;
    return style;
  }

  public HTMLStyleElement inject() {
    HTMLStyleElement style = createElement(styleBody);
    DomGlobal.document.head.appendChild(style);
    return style;
  }

  public HTMLStyleElement injectAtStart() {
    HTMLStyleElement style = createElement(styleBody);
    DomGlobal.document.head.insertBefore(style, DomGlobal.document.head.firstChild);
    return style;
  }
}
