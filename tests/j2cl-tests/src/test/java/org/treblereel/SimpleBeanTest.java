/*
 * Copyright © 2020 Treblereel
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

package org.treblereel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Before;
import org.junit.Test;
import org.treblereel.managedinstance.ComponentIface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/10/19
 */
@J2clTestInput(SimpleBeanTest.class)
public class SimpleBeanTest {
    private final App app = new App();

    @Before
    public void init() {
        new org.treblereel.AppBootstrap(app).initialize();
    }

    @Test
    public void testAppSimpleBean() {
        assertTrue(app.initialized);
        assertNotNull(app.getComponentIfaces());
    }

    @Test
    public void testManagedInstance() {
        assertTrue(app.initialized);
        assertNotNull(app.getComponentIfaces().get().getComponentName());

        List<ComponentIface> actualList = new ArrayList<>();
        Iterator<ComponentIface> iter =  app.getComponentIfaces().iterator();
        while (iter.hasNext()) {
            actualList.add(iter.next());
        }

        assertEquals(3, actualList.size());
        assertEquals("ComponentDefault", app.getComponentIfaces().get().getComponentName());
    }
}
