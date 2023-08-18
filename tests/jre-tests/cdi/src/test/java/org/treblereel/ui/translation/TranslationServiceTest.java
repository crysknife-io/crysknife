/*
 * Copyright © 2021 Treblereel
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

package org.treblereel.ui.translation;

import io.crysknife.ui.translation.client.TranslationService;
import org.junit.Test;
import org.treblereel.AbstractTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
public class TranslationServiceTest extends AbstractTest {

  @Test
  public void test1() {
    assertNotNull(
        app.beanManager.lookupBean(TranslationBeanHolder.class).getInstance().translationService);
    TranslationService service =
        app.beanManager.lookupBean(TranslationBeanHolder.class).getInstance().translationService;
    assertEquals("newLabel", service.getTranslation(Constants.ASSIGNEE_NEW));
    assertEquals("BBB", service.getTranslation("AAA"));
    assertNull(service.getTranslation("test1"));
    assertEquals("Forms generation failed for [OLOLO]",
        service.format(Constants.FormsGenerationFailure, "OLOLO"));
    assertEquals("BUNDLE", service.getTranslation("PROPERTIES"));
    assertEquals("element2", service.getTranslation("element2"));

    assertEquals("[{0}]\n {1}", service.getTranslation("element"));
    assertEquals("[A]\n B", service.format("element", "A", "B"));
  }
}
