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

package org.treblereel.produces;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.injection.typed.ClientDefinitionsCacheRegistry;
import org.treblereel.produces.dependent.Definition;
import org.treblereel.produces.dependent.DefinitionImpl;
import org.treblereel.produces.dependent.DefinitionProducer;
import org.treblereel.produces.registry.DefaultRegistryImpl;
import org.treblereel.produces.registry.Registry;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/9/21
 */
public class ProducesTest extends AbstractTest {


  @Test
  public void testBeanOneImpl() {
    assertEquals(DefaultRegistryImpl.class,
        app.beanManager.lookupBean(Registry.class).getInstance().getClass());

  }

  @Test
  public void testDefinition() {
    for (int i = 0; i < 10; i++) {
      assertEquals(DefinitionProducer.class.getCanonicalName(),
          ((DefinitionImpl) app.beanManager.lookupBean(Definition.class).getInstance()).getId());

      assertEquals(DefinitionProducer.class.getCanonicalName(), ((DefinitionImpl) (app.beanManager
          .lookupBean(DefinitionProducer.class).getInstance().getDefinition())).getId());
    }


  }

}
