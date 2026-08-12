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
package io.crysknife.samples.single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;

@J2clTestInput(HelloComponentTest.class)
public class HelloComponentTest {

  @Before
  public void setup() {
    DomGlobal.document.body.innerHTML = "";
    App app = new App();
    new AppBootstrap(app).initialize();
  }

  @Test
  public void testComponentRendered() {
    HTMLElement root = (HTMLElement) DomGlobal.document.body.firstElementChild;
    assertNotNull("HelloComponent root element should be rendered", root);
  }

  @Test
  public void testInputFieldExists() {
    HTMLInputElement input =
        (HTMLInputElement) DomGlobal.document.querySelector("[data-field='nameInput']");
    assertNotNull("Name input field should exist", input);
  }

  @Test
  public void testGreetButtonExists() {
    HTMLButtonElement button =
        (HTMLButtonElement) DomGlobal.document.querySelector("[data-field='greetBtn']");
    assertNotNull("Greet button should exist", button);
  }

  @Test
  public void testGreetingDisplayed() {
    HTMLInputElement input =
        (HTMLInputElement) DomGlobal.document.querySelector("[data-field='nameInput']");
    HTMLButtonElement button =
        (HTMLButtonElement) DomGlobal.document.querySelector("[data-field='greetBtn']");
    HTMLDivElement greeting =
        (HTMLDivElement) DomGlobal.document.querySelector("[data-field='greeting']");

    input.value = "World";
    button.click();

    assertEquals("Hello, World!", greeting.textContent);
  }
}
