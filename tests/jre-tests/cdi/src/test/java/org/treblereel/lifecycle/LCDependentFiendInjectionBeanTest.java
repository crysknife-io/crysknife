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

import jakarta.enterprise.inject.Instance;

import io.crysknife.client.internal.InstanceImpl;
import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.lifecycle.dependent.LCDependentBeanOne;
import org.treblereel.lifecycle.dependent.LCDependentBeanThree;
import org.treblereel.lifecycle.dependent.LCDependentBeanTrap;
import org.treblereel.lifecycle.dependent.LCDependentBeanTwo;
import org.treblereel.lifecycle.dependent.LCDependentFiendInjectionBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LCDependentFiendInjectionBeanTest extends AbstractTest {


    @Test
    public void test() {
        LCDependentBeanTrap.CLASSES.clear();
        LCDependentFiendInjectionBean dependent = app.beanManager.lookupBean(LCDependentFiendInjectionBean.class).getInstance();
        assertNotNull(dependent);

        app.beanManager.destroyBean(dependent);

        assertEquals(4, LCDependentBeanTrap.CLASSES.size());

        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentFiendInjectionBean.class));
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentBeanOne.class));
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentBeanTwo.class));
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentBeanThree.class));
        LCDependentFiendInjectionBean dependent2 = app.beanManager.lookupBean(LCDependentFiendInjectionBean.class).getInstance();
        assertNotEquals(dependent, dependent2);
    }

    @Test
    public void testMultipleDependentInstancesDestroyIndependently() {
        LCDependentBeanTrap.CLASSES.clear();

        LCDependentFiendInjectionBean dep1 =
            app.beanManager.lookupBean(LCDependentFiendInjectionBean.class).getInstance();
        LCDependentFiendInjectionBean dep2 =
            app.beanManager.lookupBean(LCDependentFiendInjectionBean.class).getInstance();
        assertNotNull(dep1);
        assertNotNull(dep2);
        assertNotEquals(dep1, dep2);

        app.beanManager.destroyBean(dep1);
        assertEquals(4, LCDependentBeanTrap.CLASSES.size());

        LCDependentBeanTrap.CLASSES.clear();
        app.beanManager.destroyBean(dep2);
        assertEquals(4, LCDependentBeanTrap.CLASSES.size());
    }

    @Test
    public void testInstanceDestroyTriggersCascadingPreDestroy() {
        LCDependentBeanTrap.CLASSES.clear();

        Instance<LCDependentFiendInjectionBean> instance =
            new InstanceImpl<>(app.beanManager, LCDependentFiendInjectionBean.class);
        LCDependentFiendInjectionBean bean = instance.get();
        assertNotNull(bean);

        instance.destroy(bean);

        assertEquals(4, LCDependentBeanTrap.CLASSES.size());
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentFiendInjectionBean.class));
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentBeanOne.class));
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentBeanTwo.class));
        assertTrue(LCDependentBeanTrap.CLASSES.contains(LCDependentBeanThree.class));
    }
}
