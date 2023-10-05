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

package io.crysknife.demo.client.inject;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.DomGlobal;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
@Singleton
public class BeanTwo {

    @Inject
    DependentBean dependentBean;
    private BeanOne beanOne;
    private BeanThree beanThree;

    @Inject
    public BeanTwo(BeanOne beanOne, BeanThree beanThree) {
        this.beanOne = beanOne;
        this.beanThree = beanThree;
    }

    @PostConstruct
    void init() {
        DomGlobal.console.log("i am ready");
        dependentBean.sayHello();
    }

    public void say() {
        DomGlobal.console.log(this.getClass().getCanonicalName());
        beanOne.say();
        beanThree.say();
    }

    public String callBeanOne() {
        return beanOne.callCar();
    }
}
