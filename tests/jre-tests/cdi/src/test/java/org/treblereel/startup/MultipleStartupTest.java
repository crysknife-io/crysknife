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

package org.treblereel.startup;

import java.util.List;

import org.junit.Test;
import org.treblereel.AbstractTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultipleStartupTest extends AbstractTest {

  @Test
  public void testAllStartupBeansInitialized() {
    StartupTracker tracker =
        app.beanManager.lookupBean(StartupTracker.class).getInstance();
    List<String> initialized = tracker.getInitializedBeans();

    assertEquals(2, initialized.size());
    assertTrue(initialized.contains(OnStartup.class.getCanonicalName()));
    assertTrue(initialized.contains(SecondStartupBean.class.getCanonicalName()));
  }
}
