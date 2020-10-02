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

package org.treblereel.client.mutationobserver;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MutationRecord;
import org.gwtproject.event.dom.client.ClickEvent;
//import org.gwtproject.user.client.ui.Button;
import org.jboss.elemento.IsElement;
import org.treblereel.gwt.crysknife.templates.client.annotation.DataField;
import org.treblereel.gwt.crysknife.templates.client.annotation.EventHandler;
import org.treblereel.gwt.crysknife.templates.client.annotation.Templated;
import org.treblereel.gwt.crysknife.client.BeanManager;
import org.treblereel.gwt.crysknife.mutationobserver.client.api.MutationObserver;
import org.treblereel.gwt.crysknife.mutationobserver.client.api.OnAttach;
import org.treblereel.gwt.crysknife.mutationobserver.client.api.OnDetach;
import org.treblereel.gwt.crysknife.navigation.client.local.Page;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/9/19
 */
@Singleton
@Page
@Templated(value = "mutationobserverdemo.html#mutationobserverdemo")
public class MutationObserverDemo implements IsElement<HTMLDivElement> {

    @DataField HTMLDivElement mutationobserverdemo;

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
    void onCityClick(final ClickEvent e) {
        if (test.parentNode == null) {
            container.appendChild(test);
        } else {
            container.removeChild(test);
        }
    }

    @EventHandler("reset")
    void reset(final ClickEvent e) {
        observer.addOnAttachListener(test, m -> onAttach(m));
        observer.addOnDetachListener(test, m -> onDetach(m));
        textBox.textContent = "";
    }

    @EventHandler("disconnect")
    void disconnect(final ClickEvent e) {
        observer.disconnect();
        removeAttach.disabled = true;
        removeDetach.disabled = true;
        reset.disabled = true;

        textBox.value = "disconnect";
    }

    @EventHandler("removeAttach")
    void removeOnAttachListener(final ClickEvent e) {
        observer.removeOnAttachListener(test);
        textBox.value = "";
    }

    @EventHandler("removeDetach")
    void removeOnDetachListener(final ClickEvent e) {
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
    public HTMLDivElement element() {
        return mutationobserverdemo;
    }
}
