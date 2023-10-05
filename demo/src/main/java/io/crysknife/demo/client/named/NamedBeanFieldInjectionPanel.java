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

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.demo.client.inject.named.Animal;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Dependent
//@Page(path = "ZZZZ")
@Templated("namedbeanfieldinjectionpanel.html")
public class NamedBeanFieldInjectionPanel implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    private HTMLDivElement form;

    @Inject
    @DataField
    private HTMLInputElement textBox;

    @Inject
    @DataField
    private HTMLButtonElement birdBtn;

    @Inject
    @DataField
    private HTMLButtonElement cowBtn;

    @Inject
    @DataField
    private HTMLButtonElement dogBtn;

    @Inject
    @Named("dog")
    private Animal dog;

    @Inject
    @Named("cow")
    private Animal cow;

    @Inject
    @Named("bird")
    private Animal bird;

    private void setText(String text) {
        textBox.value = text;
    }

    @Override
    public HTMLDivElement getElement() {
        return form;
    }

    @EventHandler("birdBtn")
    protected void onClickBird(@ForEvent("click") final MouseEvent event) {
        setText(bird.say());
    }

    @EventHandler("cowBtn")
    protected void onClickCow(@ForEvent("click") final MouseEvent event) {
        setText(cow.say());
    }

    @EventHandler("dogBtn")
    protected void onClickDog(@ForEvent("click") final MouseEvent event) {
        setText(dog.say());
    }

}
