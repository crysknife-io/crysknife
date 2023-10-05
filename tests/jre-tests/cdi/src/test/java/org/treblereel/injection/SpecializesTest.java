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

package org.treblereel.injection;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.injection.managedinstance.SimpleBean;
import org.treblereel.injection.qualifiers.specializes.SpecializesBeanImpl;
import org.treblereel.injection.specializes.ChildBeanSpecializes;
import org.treblereel.injection.specializes.ParentBean;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 8/26/21
 */
public class SpecializesTest extends AbstractTest {

  @Test
  public void testSpecializesBean() {
    assertEquals(SpecializesBeanImpl.class, app.specializesBeanHolder.bean.getClass());
    assertEquals(SimpleBean.class.getCanonicalName(),
        ((SpecializesBeanImpl) app.specializesBeanHolder.bean).getSimpleBean().say());
    assertEquals(SimpleBean.class.getCanonicalName(),
        ((SpecializesBeanImpl) app.specializesBeanHolder.bean).getLocalSimpleBean().say());
  }

  @Test
  public void testSpecializesBean2() {
    assertEquals(ChildBeanSpecializes.class, app.beanManager.lookupBean(ParentBean.class).getInstance().getClass());
  }
}
