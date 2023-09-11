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

import io.crysknife.client.BeanManager;
import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.lifecycle.managed.ManagedBean;
import org.treblereel.lifecycle.managed.ManagedBeanHolder;
import org.treblereel.lifecycle.managed.ManagedBeanHolderConstructors;
import org.treblereel.lifecycle.managed.ManagedBeanParent;
import org.treblereel.lifecycle.managed.ManagedBeanTrap;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ManagedBeanTest extends AbstractTest {


    @Test
    public void testDestroyBean() {
        ManagedBeanTrap.CLASSES.clear();
        BeanManager beanManager = app.beanManager;
        ManagedBeanHolder managedBeanHolder = beanManager.lookupBean(ManagedBeanHolder.class).getInstance();
        ManagedBean managedBean = managedBeanHolder.getManagedBean().get();
        assertNotNull(managedBean);
        managedBeanHolder.getManagedBean().destroy(managedBean);
        assertTrue(ManagedBeanTrap.CLASSES.contains(ManagedBean.class));
    }

    @Test
    public void testDestroyBeans() {
        ManagedBeanTrap.CLASSES.clear();
        BeanManager beanManager = app.beanManager;
        ManagedBeanHolder managedBeanHolder = beanManager.lookupBean(ManagedBeanHolder.class).getInstance();
        Set<ManagedBeanParent> managedBeans = new HashSet<>();

        managedBeanHolder.getManagedBeans().forEach(managedBeans::add);
        assertEquals(3, managedBeans.size());
        managedBeanHolder.getManagedBeans().destroyAll();
        assertEquals(3, ManagedBeanTrap.CLASSES.size());
        for (ManagedBeanParent managedBean : managedBeans) {
            if(!ManagedBeanTrap.CLASSES.contains(managedBean.getClass())) {
                throw new RuntimeException(managedBean.getClass().getName() + " not found");
            }
        }

    }

    @Test
    public void testDestroyBeanWithManagedDep() {
        ManagedBeanTrap.CLASSES.clear();
        BeanManager beanManager = app.beanManager;
        ManagedBeanHolder managedBeanHolder = beanManager.lookupBean(ManagedBeanHolder.class).getInstance();
        Set<Object> managedBeans = new HashSet<>();
        managedBeanHolder.getManagedBeans().forEach(managedBeans::add);
        managedBeans.add(managedBeanHolder.getManagedBean().get());
        beanManager.destroyBean(managedBeanHolder);


        assertEquals(4, managedBeans.size());
        assertEquals(5, ManagedBeanTrap.CLASSES.size());
        for (Object managedBean : managedBeans) {
            if(!ManagedBeanTrap.CLASSES.contains(managedBean.getClass())) {
                throw new RuntimeException(managedBean.getClass().getName() + " not found");
            }
        }
        assertTrue(ManagedBeanTrap.CLASSES.contains(ManagedBean.class));
    }

    @Test
    public void testDestroyBeanWithConstructorManagedDep() {
        ManagedBeanTrap.CLASSES.clear();
        BeanManager beanManager = app.beanManager;
        ManagedBeanHolderConstructors managedBeanHolder = beanManager.lookupBean(ManagedBeanHolderConstructors.class).getInstance();
        Set<Object> managedBeans = new HashSet<>();
        managedBeanHolder.getManagedBeans().forEach(managedBeans::add);
        managedBeans.add(managedBeanHolder.getManagedBean().get());
        beanManager.destroyBean(managedBeanHolder);

        assertEquals(4, managedBeans.size());
        assertEquals(5, ManagedBeanTrap.CLASSES.size());
        for (Object managedBean : managedBeans) {
            if(!ManagedBeanTrap.CLASSES.contains(managedBean.getClass())) {
                throw new RuntimeException(managedBean.getClass().getName() + " not found");
            }
        }
        assertTrue(ManagedBeanTrap.CLASSES.contains(ManagedBean.class));
    }

}
