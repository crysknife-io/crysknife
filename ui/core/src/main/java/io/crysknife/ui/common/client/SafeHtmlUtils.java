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

package io.crysknife.ui.common.client;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLScriptElement;
import elemental2.dom.TrustedTypePolicy;
import elemental2.dom.TrustedTypePolicyFactory;
import elemental2.dom.TrustedTypePolicyOptions;
import jsinterop.base.Js;

public final class SafeHtmlUtils {

  private static final String POLICY_NAME =
      System.getProperty("crysknife.trustedtype.policy.name", "crysknife");
  private static TrustedTypePolicy policy;
  private static boolean initialized;

  private SafeHtmlUtils() {}

  private static void ensureInitialized() {
    if (!initialized) {
      initialized = true;
      Object maybeTrustedTypes = Js.asPropertyMap(DomGlobal.window).get("trustedTypes");
      if (maybeTrustedTypes != null) {
        TrustedTypePolicyFactory factory = Js.uncheckedCast(maybeTrustedTypes);
        TrustedTypePolicyOptions opts = TrustedTypePolicyOptions.create();
        opts.setCreateHTML((input, args) -> input);
        opts.setCreateScript((input, args) -> input);
        opts.setCreateScriptURL((input, args) -> input);
        policy = factory.createPolicy(POLICY_NAME, opts);
      }
    }
  }

  public static void setInnerHTML(Element element, String html) {
    ensureInitialized();
    if (policy != null) {
      element.innerHTML = Js.uncheckedCast(policy.createHTML(html));
    } else {
      element.innerHTML = html;
    }
  }

  public static void setScriptText(HTMLScriptElement element, String script) {
    ensureInitialized();
    if (policy != null) {
      element.text = Js.uncheckedCast(policy.createScript(script));
    } else {
      element.text = script;
    }
  }

  public static void setScriptSrc(HTMLScriptElement element, String url) {
    ensureInitialized();
    if (policy != null) {
      element.src = Js.uncheckedCast(policy.createScriptURL(url));
    } else {
      element.src = url;
    }
  }
}
