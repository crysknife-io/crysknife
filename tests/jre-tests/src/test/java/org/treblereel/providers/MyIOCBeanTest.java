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

package org.treblereel.providers;

import org.junit.Test;
import org.treblereel.AbstractTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
public class MyIOCBeanTest extends AbstractTest {

  @Test
  public void test1() {
    assertNotNull(app.beanManager.lookupBean(MyIOCBeanHolder.class).getInstance());
    MyIOCBeanHolder holder = app.beanManager.lookupBean(MyIOCBeanHolder.class).getInstance();
    assertEquals(Integer.class, holder.getMybean1().getKey());
    assertEquals(Double.class, holder.getMybean1().getValue());
  }

  @Test
  public void testSingleton() {
    MyIOCBeanHolder holder = app.beanManager.lookupBean(MyIOCBeanHolder.class).getInstance();
    assertEquals(Integer.class, holder.getMyIOCSingletonBean().getKey());
    assertEquals(Double.class, holder.getMyIOCSingletonBean().getValue());

    assertEquals(Integer.class, holder.getMyIOCSingletonBean2().getKey());
    assertEquals(Double.class, holder.getMyIOCSingletonBean2().getValue());

    assertEquals(holder.getMyIOCSingletonBean2().unique, holder.getMyIOCSingletonBean().unique);

  }
}
