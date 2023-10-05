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

package org.treblereel.injection;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.injection.any.AnyTestHolder;
import org.treblereel.injection.any.YetAnotherInterface;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class AnyTest extends AbstractTest {

    @Test
    public void testAnyYetAnotherBeanManagedInstance() {
        AnyTestHolder test = app.beanManager.lookupBean(AnyTestHolder.class).getInstance();
        Set<String> yetAnotherBean = new HashSet<>();

        for(YetAnotherInterface bean: test.managedInstance) {
            yetAnotherBean.add(bean.getType());
        }

        assertEquals(3, yetAnotherBean.size());
        assertTrue(yetAnotherBean.contains(org.treblereel.injection.any.YetAnotherBeanProducer.class.getSimpleName()));
        assertTrue(yetAnotherBean.contains(org.treblereel.injection.any.YetAnotherBeanOne.class.getSimpleName()));
        assertTrue(yetAnotherBean.contains(org.treblereel.injection.any.YetAnotherBeanTwo.class.getSimpleName()));

    }

    @Test
    public void testAnyYetAnotherBeanInstance() {
        AnyTestHolder test = app.beanManager.lookupBean(AnyTestHolder.class).getInstance();
        Set<String> yetAnotherBean = new HashSet<>();

        for(YetAnotherInterface bean: test.instanceHolder) {
            yetAnotherBean.add(bean.getType());
        }

        assertEquals(3, yetAnotherBean.size());
        assertTrue(yetAnotherBean.contains(org.treblereel.injection.any.YetAnotherBeanProducer.class.getSimpleName()));
        assertTrue(yetAnotherBean.contains(org.treblereel.injection.any.YetAnotherBeanOne.class.getSimpleName()));
        assertTrue(yetAnotherBean.contains(org.treblereel.injection.any.YetAnotherBeanTwo.class.getSimpleName()));

    }

   @Test
    public void testYetAnotherQualifierInstance() {
        AnyTestHolder test = app.beanManager.lookupBean(AnyTestHolder.class).getInstance();
        Set<String> yetAnotherBean = new HashSet<>();

        for(YetAnotherInterface bean: test.instanceHolderQualifier) {
            yetAnotherBean.add(bean.getType());
        }

        assertEquals(1, yetAnotherBean.size());
        assertTrue(yetAnotherBean.contains(org.treblereel.injection.any.YetAnotherBeanTwo.class.getSimpleName()));
    }

    @Test
    public void testYetAnotherQualifierManagedInstance() {
        AnyTestHolder test = app.beanManager.lookupBean(AnyTestHolder.class).getInstance();
        Set<String> yetAnotherBean = new HashSet<>();

        for(YetAnotherInterface bean: test.managedInstanceQualifier) {
            yetAnotherBean.add(bean.getType());
        }

        assertEquals(1, yetAnotherBean.size());
        assertTrue(yetAnotherBean.contains(org.treblereel.injection.any.YetAnotherBeanTwo.class.getSimpleName()));
    }


}
