/*
 * Copyright © 2026 Treblereel
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

package io.crysknife.tests.translation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;

@J2clTestInput(TranslationRuTest.class)
public class TranslationRuTest {

  private TestMessages messages;

  @Before
  public void setup() {
    messages = new TestMessagesImpl();
    App app = new App();
    new AppBootstrap(app).initialize();
  }

  @Test
  public void testWelcomeRussian() {
    assertEquals("Добро пожаловать",
        messages.welcome());
  }

  @Test
  public void testSaveRussian() {
    assertEquals("Сохранить",
        messages.save());
  }

  @Test
  public void testCancelRussian() {
    assertEquals("Отмена", messages.cancel());
  }

  @Test
  public void testDeleteButtonRussian() {
    assertEquals("Удалить",
        messages.deleteButton());
  }

  @Test
  public void testGreetingWithParametersRussian() {
    assertEquals(
        "Привет World, у тебя 5 сообщений",
        messages.greeting("World", "5"));
  }

  @Test
  public void testI18nKeyWelcomeInDom() {
    HTMLElement el = (HTMLElement) DomGlobal.document.querySelector("[data-field='welcomeLabel']");
    assertNotNull(el);
    assertEquals("Добро пожаловать",
        el.textContent);
  }

  @Test
  public void testI18nKeySaveInDom() {
    HTMLElement el = (HTMLElement) DomGlobal.document.querySelector("[data-field='saveLabel']");
    assertNotNull(el);
    assertEquals("Сохранить",
        el.textContent);
  }

  @Test
  public void testI18nKeyDeleteInDom() {
    HTMLElement el = (HTMLElement) DomGlobal.document.querySelector("[data-field='deleteLabel']");
    assertNotNull(el);
    assertEquals("Удалить",
        el.textContent);
  }
}
