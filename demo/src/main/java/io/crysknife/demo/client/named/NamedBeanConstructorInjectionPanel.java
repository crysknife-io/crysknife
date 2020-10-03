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
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsProperty;
import org.jboss.elemento.IsElement;
import io.crysknife.demo.client.inject.named.Vehicle;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
@Templated("namedbeanconstructorinjectionpanel.html")
public class NamedBeanConstructorInjectionPanel implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    @JsProperty
    private HTMLDivElement form;

    @Inject
    @DataField
    private HTMLInputElement textBox;

    @Inject
    @DataField
    @JsProperty
    private HTMLButtonElement helicopterBtn;

    @Inject
    @DataField
    private HTMLButtonElement carBtn;

    private Vehicle car;

    private Vehicle helicopter;

    @Inject
    NamedBeanConstructorInjectionPanel(@Named("Helicopter") Vehicle helicopter, @Named("Car") Vehicle car) {
        this.car = car;
        this.helicopter = helicopter;
    }

    @PostConstruct
    public void init() {
        initBtn();
    }

    private void initBtn() {
        carBtn.addEventListener("click", evt -> setText(car.whoAmI()));
        helicopterBtn.addEventListener("click", evt -> setText(helicopter.whoAmI()));
    }

    private void setText(String text) {
        textBox.value = text;
    }

    @Override
    public HTMLDivElement element() {
        return form;
    }

    @EventHandler("carBtn")
    protected void onClickCar(@ForEvent("click") final MouseEvent event) {
        setText(car.whoAmI());
    }

    @EventHandler("helicopterBtn")
    protected void onClickHelicopter(@ForEvent("click") final MouseEvent event) {
        setText(helicopter.whoAmI());
    }
}

