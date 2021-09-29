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

import io.crysknife.client.BeanManager;
import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.injection.cycle.AbstractRegistryFactory;
import org.treblereel.injection.cycle.AdapterManager;
import org.treblereel.injection.cycle.ClientRegistryFactoryImpl;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/15/21
 */
public class CycleDepsTest extends AbstractTest {

  // @Test
  public void test() {
    BeanManager beanManager = app.beanManager;

    AbstractRegistryFactory abstractRegistryFactory = beanManager
        .<AbstractRegistryFactory>lookupBean(AbstractRegistryFactory.class).getInstance();

    AdapterManager adapterManager =
        beanManager.<AdapterManager>lookupBean(AdapterManager.class).getInstance();

    assertEquals(ClientRegistryFactoryImpl.class.getCanonicalName(),
        abstractRegistryFactory.getClass().getCanonicalName());

    assertEquals(AdapterManager.class.getCanonicalName(),
        adapterManager.getClass().getCanonicalName());
  }
}
