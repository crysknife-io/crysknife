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

package io.crysknife.tests.commons;

import static org.junit.Assert.assertEquals;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.junit.Test;
import io.crysknife.ui.common.client.injectors.ScriptInjector;

@J2clTestInput(ScriptInjectorTest.class)
public class ScriptInjectorTest {

  @Test
  public void testInjectScript() {
    String jsCode = "window.__ck_test_value = 'crysknife_works';";
    ScriptInjector.fromString(jsCode).setWindow(ScriptInjector.TOP_WINDOW).inject();

    JsPropertyMap<Object> win = Js.asPropertyMap(DomGlobal.window);
    String result = (String) win.get("__ck_test_value");
    assertEquals("crysknife_works", result);
  }

  @Test
  public void testInjectScriptWithDefaultWindow() {
    String jsCode = "window.__ck_default_win_test = 'default_works';";
    ScriptInjector.fromString(jsCode).inject();

    JsPropertyMap<Object> win = Js.asPropertyMap(DomGlobal.window);
    String result = (String) win.get("__ck_default_win_test");
    assertEquals("default_works", result);
  }
}
