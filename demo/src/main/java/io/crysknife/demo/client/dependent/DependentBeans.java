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

package io.crysknife.demo.client.dependent;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.local.Page;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.demo.client.inject.DependentBean;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.Templated;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
@Page(path = "dependent")
@Templated(value = "dependentbeans.html", stylesheet = "DependentBeans.gss")
public class DependentBeans implements IsElement<HTMLDivElement> {

    @DataField
    private HTMLDivElement form;

    @Inject
    @DataField
    protected HTMLInputElement textBox;

    @Inject
    @DataField
    private HTMLButtonElement checkBtn;

    @Inject
    DependentBean beanOne1Instance;

    @Inject
    private DependentBean beanOne2Instance;

    @Inject
    public DependentBeans(HTMLSelectElement nativeSelect,
                          HTMLDivElement form,
                          HTMLButtonElement checkBtn) {
        this.form = form;
        this.checkBtn = checkBtn;
    }

    private void setText(String text) {
        textBox.value = text;
        textBox.textContent = text;
    }

    @EventHandler("checkBtn")
    public void onFallbackInputChange(@ForEvent("click") final MouseEvent e) {
        StringBuffer sb = new StringBuffer();
        sb.append("beanOne1Instance random :");
        sb.append(beanOne1Instance.getRandom());
        sb.append(", beanOne2Instance random :");
        sb.append(beanOne2Instance.getRandom());
        sb.append(", ? equal " + (beanOne1Instance.getRandom() == beanOne2Instance.getRandom()));

        setText(sb.toString());
    }

    @PostConstruct
    public void init(){

    }

    @Override
    public HTMLDivElement getElement() {
        return form;
    }
}
