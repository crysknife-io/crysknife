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

package org.treblereel.lifecycle.dependent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class LCDependentFiendInjectionBean {


    @Inject
    private LCDependentBeanOne one;

    @Inject
    private LCDependentBeanTwo two;

    @PostConstruct
    private void init() {
        one.getClass().getCanonicalName();
        two.getClass().getCanonicalName();
    }

    @PreDestroy
    private void destroy() {
        LCDependentBeanTrap.CLASSES.add(getClass());
    }
}
