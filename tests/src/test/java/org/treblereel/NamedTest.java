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
import org.treblereel.injection.named.NamedBeanDefault;
import org.treblereel.injection.named.NamedBeanOne;
import org.treblereel.injection.named.NamedBeanTwo;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class NamedTest extends AbstractTest {

  @Test
  public void testNamedConstructorInjection() {
    assertEquals(NamedBeanDefault.class.getSimpleName(),
        app.getNamedTestBean().namedConstructorInjection.def.getClass().getSimpleName());
    assertEquals(NamedBeanOne.class.getSimpleName(),
        app.getNamedTestBean().namedConstructorInjection.one.getClass().getSimpleName());
    assertEquals(NamedBeanTwo.class.getSimpleName(),
        app.getNamedTestBean().namedConstructorInjection.two.getClass().getSimpleName());
  }

  @Test
  public void testNamedFieldInjection() {
    assertEquals(NamedBeanDefault.class.getSimpleName(),
        app.getNamedTestBean().namedFieldInjection.def.getClass().getSimpleName());
    assertEquals(NamedBeanOne.class.getSimpleName(),
        app.getNamedTestBean().namedFieldInjection.one.getClass().getSimpleName());
    assertEquals(NamedBeanTwo.class.getSimpleName(),
        app.getNamedTestBean().namedFieldInjection.two.getClass().getSimpleName());
  }
}
