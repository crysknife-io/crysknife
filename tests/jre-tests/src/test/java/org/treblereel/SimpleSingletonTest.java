/*
 * Copyright © 2020 Treblereel
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

package org.treblereel;

import org.junit.Test;
import org.treblereel.scopes.ApplicationScopedBean;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class SimpleSingletonTest extends AbstractTest {

  @Test
  public void testDependent() {
    int fieldOne = app.simpleSingletonTest.getFieldOne().getRandom();
    int fieldTwo = app.simpleSingletonTest.getFieldTwo().getRandom();
    int constrOne = app.simpleSingletonTest.getConstrOne().getRandom();
    int constrTwo = app.simpleSingletonTest.getConstrTwo().getRandom();

    assertEquals(fieldOne, fieldTwo);
    assertEquals(constrOne, constrTwo);

    ApplicationScopedBean applicationScopedBean1 =
        app.beanManager.lookupBean(ApplicationScopedBean.class).getInstance();
    ApplicationScopedBean applicationScopedBean2 =
        app.beanManager.lookupBean(ApplicationScopedBean.class).getInstance();
    assertEquals(applicationScopedBean1.getValue(), applicationScopedBean2.getValue());

  }
}
