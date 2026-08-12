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

package org.treblereel.injection.alternative;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.injection.alternative.defaultbean.AlternativeFormatter;
import org.treblereel.injection.alternative.defaultbean.Formatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AlternativeOverridesDefaultTest extends AbstractTest {

  @Test
  public void testAlternativeOverridesDefaultBean() {
    Formatter bean = app.beanManager.lookupBean(Formatter.class).getInstance();
    assertNotNull(bean);
    assertEquals(AlternativeFormatter.class, bean.getClass());
  }

  @Test
  public void testAlternativeBeanBehavior() {
    Formatter bean = app.beanManager.lookupBean(Formatter.class).getInstance();
    assertEquals("alternative:test", bean.format("test"));
  }
}
