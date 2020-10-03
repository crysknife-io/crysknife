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

package io.crysknife.demo.client.qualifiers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import org.jboss.elemento.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/15/20
 */
@ApplicationScoped
@Page
@Templated(value = "qualifiers.html")
public class Qualifiers implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    HTMLDivElement root;

    @Inject
    @DataField
    private HTMLInputElement textBox;

    @Inject
    @DataField
    private HTMLButtonElement defaultBtn;

    @Inject
    @DataField
    private HTMLButtonElement qualifierOneBtn;

    @Inject
    @DataField
    private HTMLButtonElement qualifierTwoBtn;

    @Inject
    @QualifierOne
    private QualifierBean one;

    @Inject
    @QualifierTwo
    private QualifierBean two;

    @Inject
    @Default
    private QualifierBean three;

    @EventHandler("defaultBtn")
    protected void onClickCar(@ForEvent("click") final MouseEvent event) {
        setText(three.say());
    }

    private void setText(String text) {
        textBox.value = text;
    }

    @EventHandler("qualifierOneBtn")
    protected void onClickOne(@ForEvent("click") final MouseEvent event) {
        setText(one.say());
    }

    @EventHandler("qualifierTwoBtn")
    protected void onClickTwo(@ForEvent("click") final MouseEvent event) {
        setText(two.say());
    }

    @Override
    public HTMLDivElement element() {
        return root;
    }
}