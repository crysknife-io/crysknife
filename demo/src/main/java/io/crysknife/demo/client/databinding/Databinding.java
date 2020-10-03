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

package io.crysknife.demo.client.databinding;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import org.gwtproject.event.dom.client.ClickEvent;
//import org.gwtproject.user.client.ui.TextBox;
import org.jboss.elemento.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.Templated;
/*import io.crysknife.databinding.client.api.DataBinder;
import io.crysknife.databinding.client.api.StateSync;
import io.crysknife.databinding.client.api.handler.property.PropertyChangeHandler;*/
import io.crysknife.ui.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/18/19
 */
@Singleton
@Page
@Templated(value = "databinding.html")
public class Databinding implements IsElement<HTMLDivElement> {

    @DataField HTMLDivElement root;

/*    @Inject
    protected DataBinder<Customer> dataBinder;*/
/*    @DataField
    @Inject
    protected TextBox nameBox;*/
    //@DataField
    //@Inject
    //protected MyTextBox cityBox;
    @Inject
    @DataField
    protected HTMLInputElement streetBox;
    @Inject
    @DataField
    protected HTMLInputElement ageBox;
    @Inject
    @DataField
    protected HTMLInputElement resultBox;
    @Inject
    @DataField
    protected HTMLButtonElement modelBtn;
    @Inject
    @DataField
    protected HTMLButtonElement workingModelBtn;
    @Inject
    @DataField("pauseBtn")
    protected HTMLButtonElement pause;
    @Inject
    @DataField("resumeBtn")
    protected HTMLButtonElement resume;
    private Customer customer;

    @PostConstruct
    public void init() {

/*        customer = dataBinder
                .bind(nameBox, "name")
                .bind(cityBox, "address.city")
                .bind(streetBox, "address.street.name")
                .bind(ageBox, "age")
                .getModel();

        dataBinder.addPropertyChangeHandler((PropertyChangeHandler<Customer>) event -> {
            DomGlobal.console.log("new value " + event.toString());
            onPropertyChange(event.toString());
        });*/

    }

    @Override
    public HTMLDivElement element() {
        return root;
    }

    private void onPropertyChange(String state) {
        resultBox.value = state;
    }

    //@EventHandler("cityBox")
    void onCityClick(final ClickEvent e) {
        DomGlobal.console.log("cityBox click");
    }

/*    @EventHandler("modelBtn")
    void getModel(final ClickEvent e) {
        onPropertyChange(dataBinder.getModel().toString());
    }

    @EventHandler("workingModelBtn")
    void getWorkingModel(final ClickEvent e) {
        onPropertyChange(dataBinder.getWorkingModel().toString());
    }

    @EventHandler("pauseBtn")
    void onPause(final ClickEvent e) {
        dataBinder.pause();
    }

    @EventHandler("resumeBtn")
    void onResume(final ClickEvent e) {
        dataBinder.resume(StateSync.FROM_UI);
    }*/
}
