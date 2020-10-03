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

package io.crysknife.demo.client.template;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.gwtproject.event.dom.client.ClickEvent;
import org.jboss.elemento.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.navigation.client.local.Page;

@Singleton
@Page
@Templated("templated.html")
public class TemplatedBean implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    private HTMLDivElement root;

    @Inject
    @DataField
    private HTMLButtonElement button;

    @Inject
    @DataField
    private HTMLButtonElement button1;

    @PostConstruct
    public void init() {
        button1.textContent = " PressMe";
    }

    @EventHandler("button")
    public void onClick(final ClickEvent e) {

    }

    public HTMLDivElement element() {
        return root;
    }
}
