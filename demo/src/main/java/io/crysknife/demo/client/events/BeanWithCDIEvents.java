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

package io.crysknife.demo.client.events;

import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/31/19
 */
@Singleton
@Page
@Templated("beanwithcdievents.html")
public class BeanWithCDIEvents implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    protected HTMLDivElement root;

    @Inject
    @DataField
    protected HTMLInputElement textBox;

    @Inject
    protected Event<User> eventUser;

    @Inject
    protected Event<Address> eventAddress;

    @Inject
    @DataField
    protected HTMLButtonElement sendUserEvent, sendAddressEvent;

    @PostConstruct
    private void init() {
        initBtn();
    }

    private void initBtn() {
        sendUserEvent.addEventListener("click", evt -> {
            User user = new User();
            user.setId(new Random().nextInt());
            user.setName("IAMUSER");
            eventUser.fire(user);
        });

        sendAddressEvent.addEventListener("click", evt -> {
            Address address = new Address();
            address.setId(new Random().nextInt());
            address.setName("Redhat");
            eventAddress.fire(address);
        });
    }

    private void onUserEvent(@Observes User user) {
        setText(user.toString());
    }

    private void onAddressEvent(@Observes Address address) {
        setText(address.toString());
    }

    private void setText(String text) {
        textBox.value = text;
    }

    @Override
    public HTMLDivElement getElement() {
        return root;
    }

    @EventHandler("sendAddressEvent")
    private void sendAddressEvent(@ForEvent("click") final MouseEvent event) {
        Address address = new Address();
        address.setId(new Random().nextInt());
        address.setName("Redhat");
        eventAddress.fire(address);
    }

    @EventHandler("sendUserEvent")
    private void sendUserEvent(@ForEvent("click") final MouseEvent event) {
        User user = new User();
        user.setId(new Random().nextInt());
        user.setName("IAMUSER");
        eventUser.fire(user);
    }
}
