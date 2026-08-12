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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;

@J2clTestInput(TemplatedComponentTest.class)
public class TemplatedComponentTest {

  @Before
  public void setup() {
    App app = new App();
    new AppBootstrap(app).initialize();
  }

  @Test
  public void testTemplatedComponentRendered() {
    HTMLElement element = (HTMLElement) DomGlobal.document.querySelector("[data-field='content']");
    assertNotNull(element);
  }

  @Test
  public void testStyledComponentRendered() {
    HTMLElement element = (HTMLElement) DomGlobal.document.querySelector(".styled-component");
    assertNotNull(element);
  }

  @Test
  public void testStyleInjected() {
    boolean found = false;
    for (int i = 0; i < DomGlobal.document.head.childNodes.length; i++) {
      if ("STYLE".equals(DomGlobal.document.head.childNodes.getAt(i).nodeName)) {
        String text = DomGlobal.document.head.childNodes.getAt(i).textContent;
        if (text != null && text.contains(".styled-component")) {
          found = true;
          break;
        }
      }
    }
    assertTrue("StyleInjector should have injected CSS for .styled-component", found);
  }
}
