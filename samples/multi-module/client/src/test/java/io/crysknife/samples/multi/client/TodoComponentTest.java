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
package io.crysknife.samples.multi.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;

@J2clTestInput(TodoComponentTest.class)
public class TodoComponentTest {

  @Before
  public void setup() {
    DomGlobal.document.body.innerHTML = "";
    App app = new App();
    new AppBootstrap(app).initialize();
  }

  @Test
  public void testComponentRendered() {
    HTMLElement root = (HTMLElement) DomGlobal.document.body.firstElementChild;
    assertNotNull("TodoComponent root element should be rendered", root);
  }

  @Test
  public void testInputFieldExists() {
    HTMLInputElement input =
        (HTMLInputElement) DomGlobal.document.querySelector("[data-field='todoInput']");
    assertNotNull("Todo input field should exist", input);
    assertEquals("What needs to be done?", input.placeholder);
  }

  @Test
  public void testAddButtonExists() {
    HTMLButtonElement button =
        (HTMLButtonElement) DomGlobal.document.querySelector("[data-field='addBtn']");
    assertNotNull("Add button should exist", button);
  }

  @Test
  public void testAddTodo() {
    HTMLInputElement input =
        (HTMLInputElement) DomGlobal.document.querySelector("[data-field='todoInput']");
    HTMLButtonElement button =
        (HTMLButtonElement) DomGlobal.document.querySelector("[data-field='addBtn']");
    HTMLDivElement todoList =
        (HTMLDivElement) DomGlobal.document.querySelector("[data-field='todoList']");

    input.value = "Buy milk";
    button.click();

    assertNotNull("Todo item should be added", todoList.firstElementChild);
    assertTrue(todoList.firstElementChild.textContent.contains("Buy milk"));
  }

  @Test
  public void testToggleTodo() {
    HTMLInputElement input =
        (HTMLInputElement) DomGlobal.document.querySelector("[data-field='todoInput']");
    HTMLButtonElement button =
        (HTMLButtonElement) DomGlobal.document.querySelector("[data-field='addBtn']");
    HTMLDivElement todoList =
        (HTMLDivElement) DomGlobal.document.querySelector("[data-field='todoList']");

    input.value = "Buy milk";
    button.click();

    HTMLElement item = (HTMLElement) todoList.firstElementChild;
    item.click();

    HTMLElement toggled = (HTMLElement) todoList.firstElementChild;
    assertTrue("Toggled item should have todo-done class",
        toggled.className.contains("todo-done"));
  }
}
