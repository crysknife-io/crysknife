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
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLStyleElement;
import jsinterop.annotations.JsFunction;

public class StyleInjector {

  public HTMLDocument document = DomGlobal.document;

  private final HTMLElement styleElement;

  private StyleInjector(HTMLElement styleElement) {
    this.styleElement = styleElement;
  }

  public static StyleInjector fromString(String contents) {
    HTMLStyleElement style = (HTMLStyleElement) DomGlobal.document.createElement("style");
    style.setAttribute("type", "text/css");
    style.textContent = contents;
    return new StyleInjector(style);
  }

  public static StyleInjector fromUrl(String url) {
    return fromUrl(url, null, null);
  }

  public static StyleInjector fromUrl(String url, Callback onResolve) {
    return fromUrl(url, onResolve, null);
  }

  public static StyleInjector fromUrl(String url, Callback onResolve, Callback onReject) {
    HTMLElement link = (HTMLElement) DomGlobal.document.createElement("link");
    link.setAttribute("rel", "stylesheet");
    if (onResolve != null) {
      link.onload = (e) -> onResolve.accept(link);
    }
    if (onReject != null) {
      link.onerror =
          (e) -> {
            onReject.accept(link);
            return null;
          };
    }
    link.setAttribute("href", url);
    return new StyleInjector(link);
  }

  public StyleInjector setDocument(HTMLDocument document) {
    this.document = document;
    return this;
  }

  public void inject() {
    document.head.appendChild(styleElement);
  }

  public void injectAtStart() {
    document.head.insertBefore(styleElement, document.head.firstChild);
  }

  @JsFunction
  @FunctionalInterface
  public interface Callback {
    void accept(HTMLElement element);
  }
}
