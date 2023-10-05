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
import org.treblereel.injection.alternative.AlternativeBean;
import org.treblereel.injection.alternative.BeanParent;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class AlternativeTest extends AbstractTest {


    @Test
    public void test() {
        BeanParent alternativeBean = app.beanManager.lookupBean(BeanParent.class).getInstance();
        assertNotNull(alternativeBean);
        assertEquals(alternativeBean.getClass(), AlternativeBean.class);
    }


}
