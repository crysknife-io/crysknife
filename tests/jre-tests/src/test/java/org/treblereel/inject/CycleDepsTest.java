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

package org.treblereel.inject;

import io.crysknife.annotation.CircularDependency;
import io.crysknife.client.BeanManager;
import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.injection.cycle.AbstractRegistryFactory;
import org.treblereel.injection.cycle.AdapterManager;
import org.treblereel.injection.cycle.ClientRegistryFactoryImpl;
import org.treblereel.injection.cycle.simple.SimpleBeanOne;
import org.treblereel.injection.cycle.simple.SimpleBeanOneImpl;
import org.treblereel.injection.cycle.simple.SimpleBeanOneImpl_Factory;
import org.treblereel.injection.cycle.simple.SimpleBeanTwo;
import org.treblereel.injection.cycle.simple.SimpleBeanTwoImpl;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/15/21
 */
public class CycleDepsTest extends AbstractTest {

  @Test
  public void testSimple() {
    BeanManager beanManager = app.beanManager;
    SimpleBeanOne simpleBeanOne = beanManager.lookupBean(SimpleBeanOne.class).getInstance();
    SimpleBeanTwo simpleBeanTwo = beanManager.lookupBean(SimpleBeanTwo.class).getInstance();

    SimpleBeanOneImpl simpleBeanOne1 = (SimpleBeanOneImpl) simpleBeanOne;
    SimpleBeanTwoImpl simpleBeanTwo2 = (SimpleBeanTwoImpl) simpleBeanTwo;

    assertEquals(
        "org.treblereel.injection.cycle.simple.SimpleBeanOneImpl_Factory.ProxySimpleBeanOneImpl",
        simpleBeanOne1.getClass().getCanonicalName());

    assertEquals("SimpleBeanOneImpl", simpleBeanOne.whoAmI());
    assertEquals("SimpleBeanTwoImpl", simpleBeanOne.whoIsDep());
    assertEquals("SimpleBeanOneImpl.init", simpleBeanOne.getPostConstruct());

    assertEquals(
        "org.treblereel.injection.cycle.simple.SimpleBeanTwoImpl_Factory.ProxySimpleBeanTwoImpl",
        simpleBeanTwo.getClass().getCanonicalName());

    assertEquals("SimpleBeanTwoImpl", simpleBeanTwo.whoAmI());
    assertEquals("SimpleBeanOneImpl", simpleBeanTwo.whoIsDep());
    assertEquals("SimpleBeanTwoImpl.init", simpleBeanTwo.getPostConstruct());

    assertEquals("FieldInjectBean", simpleBeanOne1.fieldInjectBean.hello());
    assertEquals("FieldInjectBean", simpleBeanTwo2.fieldInjectBean.hello());

  }

  @Test
  public void test() {
    BeanManager beanManager = app.beanManager;

    AbstractRegistryFactory abstractRegistryFactory = beanManager
        .<AbstractRegistryFactory>lookupBean(AbstractRegistryFactory.class).getInstance();

    AdapterManager adapterManager =
        beanManager.<AdapterManager>lookupBean(AdapterManager.class).getInstance();

    assertEquals(
        "org.treblereel.injection.cycle.ClientRegistryFactoryImpl_Factory.ProxyClientRegistryFactoryImpl",
        abstractRegistryFactory.getClass().getCanonicalName());

    assertEquals(
        "org.treblereel.injection.cycle.AdapterManagerImpl_Factory.ProxyAdapterManagerImpl",
        adapterManager.getClass().getCanonicalName());
  }
}
