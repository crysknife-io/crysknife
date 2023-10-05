/*
 * Copyright © 2023 Treblereel
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

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.lifecycle.dependent.LCDependentBeanOne;
import org.treblereel.lifecycle.dependent.LCDependentBeanThree;
import org.treblereel.lifecycle.dependent.LCDependentBeanTrap;
import org.treblereel.lifecycle.dependent.LCDependentBeanTwo;
import org.treblereel.lifecycle.dependent.LCSingletonFiendInjectionBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DependentBeanPreDestroyFieldInjectionTest extends AbstractTest {

    @Test
    public void test() {
        LCDependentBeanTrap.CLASSES.clear();
        LCSingletonFiendInjectionBean singletonBean = app.beanManager.lookupBean(LCSingletonFiendInjectionBean.class).getInstance();
        assertNotNull(singletonBean);
        app.beanManager.destroyBean(singletonBean);

        assertEquals(4, LCDependentBeanTrap.CLASSES.size());

        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCSingletonFiendInjectionBean.class));
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentBeanOne.class));
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentBeanTwo.class));
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentBeanThree.class));
        LCSingletonFiendInjectionBean singletonBean2 = app.beanManager.lookupBean(LCSingletonFiendInjectionBean.class).getInstance();
        assertNotEquals(singletonBean, singletonBean2);

    }

}
