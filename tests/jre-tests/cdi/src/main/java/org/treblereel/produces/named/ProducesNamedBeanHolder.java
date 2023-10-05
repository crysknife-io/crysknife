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

package org.treblereel.produces.named;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
public class ProducesNamedBeanHolder {


    @Inject
    @Default
    private ParentBean parentBeanDefault;

    @Inject
    private ParentBean parentBean;

    @Inject
    @Named("nameOne")
    private ParentBean parentBeanNamedOne;

    private ParentBean parentBeanConstructor;
    private ParentBean parentBeanDefaultConstructor;

    private ParentBean parentBeanNamedOneConstructor;


    @Inject
    public ProducesNamedBeanHolder(ParentBean parentBeanConstructor, @Default ParentBean parentBeanDefaultConstructor, @Named("nameOne") ParentBean parentBeanNamedOneConstructor) {
        this.parentBeanConstructor = parentBeanConstructor;
        this.parentBeanDefaultConstructor = parentBeanDefaultConstructor;
        this.parentBeanNamedOneConstructor = parentBeanNamedOneConstructor;
    }


    public ParentBean getParentBean() {
        return parentBean;
    }

    public ParentBean getParentBeanDefault() {
        return parentBeanDefault;
    }

    public ParentBean getParentBeanNamedOne() {
        return parentBeanNamedOne;
    }

    public ParentBean getParentBeanConstructor() {
        return parentBeanConstructor;
    }

    public ParentBean getParentBeanDefaultConstructor() {
        return parentBeanDefaultConstructor;
    }

    public ParentBean getParentBeanNamedOneConstructor() {
        return parentBeanNamedOneConstructor;
    }
}
