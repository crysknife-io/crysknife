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

package org.treblereel.ui.translation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.treblereel.AbstractTest;

public class TranslationBundleTest extends AbstractTest {

  @Test
  public void testAppMessagesInjected() {
    assertNotNull(app.appMessagesBeanHolder);
    assertNotNull(app.appMessagesBeanHolder.messages);
  }

  @Test
  public void testStubMethodsThrowInJre() {
    // In JRE mode (not J2CL), the stub methods throw UnsupportedOperationException
    // because native.js is not loaded. This verifies the stub was generated correctly.
    assertThrows(UnsupportedOperationException.class,
        () -> app.appMessagesBeanHolder.messages.welcome());
  }

  @Test
  public void testStubMethodsWithParamsThrowInJre() {
    assertThrows(UnsupportedOperationException.class,
        () -> app.appMessagesBeanHolder.messages.greeting("test", "5"));
  }
}
