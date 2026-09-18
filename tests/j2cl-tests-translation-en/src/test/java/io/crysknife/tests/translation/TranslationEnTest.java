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

@J2clTestInput(TranslationEnTest.class)
public class TranslationEnTest {

  private TestMessages messages;

  @Before
  public void setup() {
    messages = new TestMessagesImpl();
    App app = new App();
    new AppBootstrap(app).initialize();
  }

  @Test
  public void testWelcomeDefaultValue() {
    assertEquals("Welcome", messages.welcome());
  }

  @Test
  public void testSaveDefaultValue() {
    assertEquals("Save", messages.save());
  }

  @Test
  public void testCancelDefaultValue() {
    assertEquals("Cancel", messages.cancel());
  }

  @Test
  public void testDeleteButtonCustomKey() {
    assertEquals("Delete", messages.deleteButton());
  }

  @Test
  public void testGreetingWithParameters() {
    assertEquals("Hello World, you have 5 messages", messages.greeting("World", "5"));
  }

  @Test
  public void testI18nKeyWelcomeInDom() {
    HTMLElement el = (HTMLElement) DomGlobal.document.querySelector("[data-field='welcomeLabel']");
    assertNotNull(el);
    assertEquals("Welcome", el.textContent);
  }

  @Test
  public void testDefaultWithRenamablePlaceholder() {
    assertEquals("Good bye Billy !", messages.farewell("Billy"));
  }

  @Test
  public void testRepeatedAndReorderedPlaceholders() {
    assertEquals("two / one / two", messages.reordered("one", "two"));
  }

  @Test
  public void testHtmlEscapesMessageButNotPlaceholder() {
    assertEquals("&lt;b><em>Billy</em>&lt;/b>", messages.escapedHtml("<em>Billy</em>"));
  }

  @Test
  public void testUnescapeHtmlEntitiesPreservesPlaceholder() {
    assertEquals("<b>&amp;</b> &", messages.unescapedHtml("&amp;"));
  }

  @Test
  public void testI18nKeySaveInDom() {
    HTMLElement el = (HTMLElement) DomGlobal.document.querySelector("[data-field='saveLabel']");
    assertNotNull(el);
    assertEquals("Save", el.textContent);
  }

  @Test
  public void testI18nKeyDeleteInDom() {
    HTMLElement el = (HTMLElement) DomGlobal.document.querySelector("[data-field='deleteLabel']");
    assertNotNull(el);
    assertEquals("Delete", el.textContent);
  }
}
