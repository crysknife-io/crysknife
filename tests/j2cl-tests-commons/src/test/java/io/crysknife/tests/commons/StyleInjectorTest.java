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
import static org.junit.Assert.assertTrue;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.junit.Test;
import io.crysknife.ui.common.client.injectors.StyleInjector;

@J2clTestInput(StyleInjectorTest.class)
public class StyleInjectorTest {

  @Test
  public void testStyleInjection() {
    String css = ".ck-test-text { font-weight: bold; }";

    StyleInjector.fromString(css).inject();

    HTMLDivElement testElement = (HTMLDivElement) DomGlobal.document.createElement("div");
    testElement.id = "ckTestElement";
    testElement.className = "ck-test-text";
    testElement.textContent = "Hello, world!";
    DomGlobal.document.body.append(testElement);

    HTMLDivElement tested = (HTMLDivElement) DomGlobal.document.getElementById("ckTestElement");
    assertEquals("ck-test-text", tested.className);

    Window window = Js.uncheckedCast(DomGlobal.window);
    assertEquals("700", window.getComputedStyle(tested).fontWeight);
  }

  @Test
  public void testStyleInjectionAtStart() {
    String css = ".ck-test-bg { background-color: yellow; }";

    StyleInjector.fromString(css).injectAtStart();

    HTMLDivElement testElement = (HTMLDivElement) DomGlobal.document.createElement("div");
    testElement.id = "ckTestBgElement";
    testElement.className = "ck-test-bg";
    testElement.textContent = "Background test";
    DomGlobal.document.body.append(testElement);

    HTMLDivElement tested = (HTMLDivElement) DomGlobal.document.getElementById("ckTestBgElement");

    Window window = Js.uncheckedCast(DomGlobal.window);
    assertEquals("rgb(255, 255, 0)", window.getComputedStyle(tested).backgroundColor);
  }

  @Test
  public void testStyleInjectionFromUrl() {
    StyleInjector.fromUrl("test-style.css").inject();

    boolean found = false;
    for (int i = 0; i < DomGlobal.document.head.childNodes.length; i++) {
      if ("LINK".equals(DomGlobal.document.head.childNodes.getAt(i).nodeName)) {
        HTMLElement link = (HTMLElement) DomGlobal.document.head.childNodes.getAt(i);
        if ("test-style.css".equals(link.getAttribute("href"))) {
          assertEquals("stylesheet", link.getAttribute("rel"));
          found = true;
          break;
        }
      }
    }
    assertTrue(found);
  }

  @Test
  public void testStyleInjectionUsesTypeAttribute() {
    String css = ".ck-type-test { color: red; }";
    StyleInjector.fromString(css).inject();

    boolean found = false;
    for (int i = 0; i < DomGlobal.document.head.childNodes.length; i++) {
      if ("STYLE".equals(DomGlobal.document.head.childNodes.getAt(i).nodeName)) {
        HTMLElement style = (HTMLElement) DomGlobal.document.head.childNodes.getAt(i);
        if (style.textContent != null && style.textContent.contains(".ck-type-test")) {
          assertEquals("text/css", style.getAttribute("type"));
          found = true;
          break;
        }
      }
    }
    assertTrue(found);
  }
}
