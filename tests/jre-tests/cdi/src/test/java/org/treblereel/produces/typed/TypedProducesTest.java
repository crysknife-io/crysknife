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

package org.treblereel.produces.typed;

import org.junit.Test;
import org.treblereel.AbstractTest;

import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/4/21
 */
public class TypedProducesTest extends AbstractTest {


  @Test
  public void test() {
    TypedProducesTestHolder holder =
        app.beanManager.lookupBean(TypedProducesTestHolder.class).getInstance();
    assertNotNull(holder);
    assertNotNull(holder.jQueryPopover);
  }
}
