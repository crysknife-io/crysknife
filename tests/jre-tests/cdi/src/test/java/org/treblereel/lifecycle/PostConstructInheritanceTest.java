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

package org.treblereel.lifecycle;

import java.util.List;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.lifecycle.inheritance.ChildLifecycleBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PostConstructInheritanceTest extends AbstractTest {

  @Test
  public void testParentAndChildPostConstructBothRun() {
    ChildLifecycleBean bean =
        app.beanManager.lookupBean(ChildLifecycleBean.class).getInstance();
    assertNotNull(bean);

    List<String> initOrder = bean.getInitOrder();
    assertEquals(2, initOrder.size());
    assertTrue(initOrder.contains("parent"));
    assertTrue(initOrder.contains("child"));
  }

  @Test
  public void testParentPostConstructRunsBeforeChild() {
    ChildLifecycleBean bean =
        app.beanManager.lookupBean(ChildLifecycleBean.class).getInstance();

    List<String> initOrder = bean.getInitOrder();
    assertEquals(0, initOrder.indexOf("parent"));
    assertEquals(1, initOrder.indexOf("child"));
  }
}
