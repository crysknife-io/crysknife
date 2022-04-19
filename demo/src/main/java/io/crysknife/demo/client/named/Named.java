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

package io.crysknife.demo.client.named;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/12/20
 */
@ApplicationScoped
@Page
public class Named implements IsElement<HTMLDivElement> {

    @Inject
    private NamedBeanConstructorInjectionPanel constructor;

    @Inject
    private NamedBeanFieldInjectionPanel field;

    @Inject
    private HTMLDivElement root;

    @PostConstruct
    public void init() {
        root.appendChild(constructor.getElement());
        root.appendChild(field.getElement());

    }

    @Override
    public HTMLDivElement getElement() {
        return root;
    }
}
