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

package org.treblereel.lifecycle.managed;


import io.crysknife.client.ManagedInstance;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class ManagedBeanHolder {

    @Inject
    private ManagedInstance<ManagedBean> managedBean;

    @Inject
    private ManagedInstance<ManagedBeanParent> managedBeans;


    public ManagedInstance<ManagedBean> getManagedBean() {
        return managedBean;
    }

    public ManagedInstance<ManagedBeanParent> getManagedBeans() {
        return managedBeans;
    }

    @PreDestroy
    public void destroy() {
        ManagedBeanTrap.CLASSES.add(getClass());
    }

}
