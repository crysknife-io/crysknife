/*
 * Copyright © 2022 Treblereel
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

package org.treblereel.predestroy;

import org.junit.Test;
import org.treblereel.AbstractTest;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class PreDestroySimpleSingletonTest extends AbstractTest {


    @Test
    public void test() {
        AtomicBoolean isDisposed = new AtomicBoolean();
        Runnable disposed = () -> isDisposed.getAndSet(true);

        PreDestroySimpleSingleton bean = app.beanManager.lookupBean(PreDestroySimpleSingleton.class).getInstance();
        bean.setCallback(disposed);

        int check = bean.check;
        app.beanManager.destroyBean(bean);

        PreDestroySimpleSingleton bean2 = app.beanManager.lookupBean(PreDestroySimpleSingleton.class).getInstance();

        assertTrue(isDisposed.get());
        assertNotEquals(check, bean2.check);
    }
}
