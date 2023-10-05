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

import elemental2.core.Reflect;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLScriptElement;
import elemental2.dom.Window;
import jsinterop.annotations.JsFunction;

public class ScriptInjector {

  public static final Window TOP_WINDOW = DomGlobal.window;
  public Window window;

  private HTMLScriptElement scriptElement;

  private ScriptInjector(HTMLScriptElement scriptElement) {
    this.scriptElement = scriptElement;
  }

  public static ScriptInjector fromString(String contents) {
    return fromString(contents, null, null);
  }

  public static ScriptInjector fromString(String contents, Callback onResolve) {
    return fromString(contents, onResolve, null);
  }

  public static ScriptInjector fromString(String contents, Callback onResolve, Callback onReject) {
    HTMLScriptElement element = createElement(onResolve, onReject);
    element.text = contents;
    return new ScriptInjector(element);
  }

  public static ScriptInjector fromUrl(String url) {
    return fromUrl(url, null, null);
  }

  public static ScriptInjector fromUrl(String url, Callback onResolve) {
    return fromUrl(url, onResolve, null);
  }

  public static ScriptInjector fromUrl(String url, Callback onResolve, Callback onReject) {
    HTMLScriptElement element = createElement(onResolve, onReject);
    element.src = url;
    return new ScriptInjector(element);
  }

  private static HTMLScriptElement createElement(Callback onResolve, Callback onReject) {
    HTMLScriptElement script = (HTMLScriptElement) DomGlobal.document.createElement("script");
    script.addEventListener("load", (e) -> {
      if (onResolve != null) {
        onResolve.accept(script);
      }
    });

    script.addEventListener("error", (e) -> {
      if (onReject != null) {
        onReject.accept(script);
      }
    });
    script.type = "text/javascript";
    return script;
  }

  public ScriptInjector setWindow(Window window) {
    this.window = window;
    return this;
  }

  public void inject() {
    ((HTMLDocument) Reflect.get(window, "document")).head.appendChild(scriptElement);
  }

  @JsFunction
  @FunctionalInterface
  public interface Callback {
    void accept(HTMLScriptElement script);
  }
}
