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

package io.crysknife.demo.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import io.crysknife.demo.client.events.Address;
import io.crysknife.demo.client.events.User;
import io.crysknife.demo.client.named.NamedBeanConstructorInjectionPanel;
import io.crysknife.annotation.Application;
import io.crysknife.annotation.ComponentScan;
import io.crysknife.ui.navigation.client.local.DefaultPage;
import io.crysknife.ui.navigation.client.local.Navigation;

@Application
@ComponentScan("io.crysknife.demo.client")
public class App {

    @Inject
    private HTMLDivElement toast;

    @Inject
    private NamedBeanConstructorInjectionPanel namedBeanConstructorInjectionPanel;

    @Inject
    private Main main;

    @Inject
    private Navigation navigation;

    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init() {
        DomGlobal.document.body.appendChild(main.element());
        initToast();
        navigation.goToWithRole(DefaultPage.class);
    }

    private void initToast() {
        toast.id = "snackbar";
        toast.textContent = "LuckyMe";

        DomGlobal.document.body.appendChild(toast);
    }

    void onUserEvent(@Observes User user) {
        toast.className = "show";
        toast.textContent = "App : onEvent " + user.toString();

        DomGlobal.setTimeout(p0 -> toast.className = toast.className.replace("show", ""), 3000);
    }

    void onAddressEvent(@Observes Address address) {
        toast.className = "show";
        toast.textContent = "App : onEvent " + address.toString();

        DomGlobal.setTimeout(p0 -> toast.className = toast.className.replace("show", ""), 3000);
    }
}
