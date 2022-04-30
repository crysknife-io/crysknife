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

package io.crysknife.demo.client.mutationobserver;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.MutationRecord;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.client.BeanManager;
import io.crysknife.ui.mutationobserver.client.api.MutationObserver;
import io.crysknife.ui.mutationobserver.client.api.OnAttach;
import io.crysknife.ui.mutationobserver.client.api.OnDetach;
import io.crysknife.ui.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/9/19
 */
@Singleton
@Page
@Templated(value = "mutationobserverdemo.html#mutationobserverdemo", stylesheet = "style.css")
public class MutationObserverDemo implements IsElement<HTMLDivElement> {

    @DataField
    @Inject
    private HTMLDivElement mutationobserverdemo;

    @DataField
    @Inject
    protected HTMLButtonElement checkBtn;

    @DataField
    @Inject
    protected HTMLDivElement container;

    @DataField
    @Inject
    protected HTMLInputElement textBox;

    @Inject
    protected HTMLButtonElement test;

    @Inject
    @DataField
    protected HTMLButtonElement removeAttach, removeDetach, reset, disconnect;

    @Inject
    protected MutationObserver observer;

    @Inject
    protected BeanManager beanManager;

    @PostConstruct
    protected void init() {
        test.textContent = "I AM A TEST CONTENT";
    }

    @EventHandler("checkBtn")
    private void onCityClick(@ForEvent("click") final MouseEvent e) {
        if (test.parentNode == null) {
            container.appendChild(test);
        } else {
            container.removeChild(test);
        }
    }

    @EventHandler("reset")
    private void reset(@ForEvent("click") final MouseEvent e) {
        observer.addOnAttachListener(test, m -> onAttach(m));
        observer.addOnDetachListener(test, m -> onDetach(m));
        textBox.textContent = "";
    }

    @EventHandler("disconnect")
    private void disconnect(@ForEvent("click") final MouseEvent e) {
        observer.disconnect();
        removeAttach.disabled = true;
        removeDetach.disabled = true;
        reset.disabled = true;

        textBox.value = "disconnect";
    }

    @EventHandler("removeAttach")
    private void removeOnAttachListener(@ForEvent("click") final MouseEvent e) {
        observer.removeOnAttachListener(test);
        textBox.value = "";
    }

    @EventHandler("removeDetach")
    private void removeOnDetachListener(@ForEvent("click") final MouseEvent e) {
        observer.removeOnDetachListener(test);
        textBox.value = "";
    }

    @OnAttach("test")
    protected void onAttach(MutationRecord mutationRecord) {
        DomGlobal.console.log("on attach " + mutationRecord);
        textBox.value = "    on attach " + mutationRecord;
    }

    @OnDetach("test")
    protected void onDetach(MutationRecord mutationRecord) {
        DomGlobal.console.log("on detach " + mutationRecord);
        textBox.value = "    on detach " + mutationRecord;
    }

    @Override
    public HTMLDivElement getElement() {
        return mutationobserverdemo;
    }
}
