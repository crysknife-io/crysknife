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

package io.crysknife.demo.client.singletonbeans;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import org.jboss.elemento.IsElement;
import io.crysknife.demo.client.inject.BeanOne;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/22/19
 */
@Singleton
@Page
@Templated("singletonbeans.html")
public class SingletonBeans implements IsElement<HTMLDivElement> {

    @Inject
    @DataField("root")
    private HTMLDivElement form;

    @Inject
    @DataField("input")
    private HTMLInputElement textBox;

    @Inject
    @DataField
    private HTMLButtonElement checkBtn;

    private BeanOne beanOne1Instance;

    private BeanOne beanOne2Instance;

    @Inject
    public SingletonBeans(BeanOne beanOne1Instance, BeanOne beanOne2Instance) {
        this.beanOne1Instance = beanOne1Instance;
        this.beanOne2Instance = beanOne2Instance;
    }

    @PostConstruct
    public void init() {
    }

    private void setText(String text) {
        textBox.value = text;
    }

    @Override
    public HTMLDivElement element() {
        return form;
    }

    @EventHandler("checkBtn")
    protected void onClick(@ForEvent("click") final MouseEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("beanOne1Instance random :");
        sb.append(beanOne1Instance.getRandom());
        sb.append(", beanOne2Instance random :");
        sb.append(beanOne2Instance.getRandom());
        sb.append(", ? equal " + (beanOne1Instance.getRandom() == beanOne2Instance.getRandom()));
        setText(sb.toString());
    }
}