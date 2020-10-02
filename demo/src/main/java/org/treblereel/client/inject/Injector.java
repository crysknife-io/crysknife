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

package org.treblereel.client.inject;

import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import org.treblereel.client.inject.iface.IBean;

@Singleton
public class Injector {

    @Inject
    private BeanOne beanOne;

    @Inject
    private BeanTwo beanTwo;

    @Inject
    private IBean iBean;

    public void say() {
        DomGlobal.console.log(this.getClass().getCanonicalName());
        beanOne.say();
        beanTwo.say();
    }

    public String callBeanTwo() {
        return beanTwo.callBeanOne();
    }
}
