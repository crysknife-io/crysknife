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

package org.treblereel.providers;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.providers.provider.IOCProviderBean;
import org.treblereel.providers.provider.IOCProviderHolder;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 12/4/21
 */
public class ProviderTest extends AbstractTest {

  @Test
  public void test1() {
    IOCProviderHolder holder = app.beanManager.lookupBean(IOCProviderHolder.class).getInstance();
    assertEquals(IOCProviderBean.class, holder.iocProviderBean.getClass());
    assertEquals(holder.iocProviderBean.unique, holder.iocProviderBean2.unique);
  }
}
