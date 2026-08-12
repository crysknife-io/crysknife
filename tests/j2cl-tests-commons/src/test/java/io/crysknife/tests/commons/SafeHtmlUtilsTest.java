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
import static org.junit.Assert.assertNotNull;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.junit.Test;
import io.crysknife.ui.common.client.SafeHtmlUtils;

@J2clTestInput(SafeHtmlUtilsTest.class)
public class SafeHtmlUtilsTest {

  @Test
  public void testSetInnerHTML() {
    HTMLDivElement div = (HTMLDivElement) DomGlobal.document.createElement("div");
    SafeHtmlUtils.setInnerHTML(div, "<span id=\"safe-test\">Hello</span>");

    assertEquals("<span id=\"safe-test\">Hello</span>", div.innerHTML);

    HTMLElement span = (HTMLElement) div.querySelector("#safe-test");
    assertNotNull(span);
    assertEquals("Hello", span.textContent);
  }

  @Test
  public void testSetInnerHTMLEmpty() {
    HTMLDivElement div = (HTMLDivElement) DomGlobal.document.createElement("div");
    div.textContent = "existing content";

    SafeHtmlUtils.setInnerHTML(div, "");
    assertEquals("", div.innerHTML);
  }

  @Test
  public void testSetInnerHTMLNested() {
    HTMLDivElement div = (HTMLDivElement) DomGlobal.document.createElement("div");
    SafeHtmlUtils.setInnerHTML(div, "<div><p>Nested</p></div>");

    assertEquals(1, div.childElementCount);
    assertEquals("Nested", div.querySelector("p").textContent);
  }

  @Test
  public void testDefaultPolicyName() {
    assertEquals("crysknife",
        System.getProperty("crysknife.trustedtype.policy.name", "crysknife"));
  }
}
