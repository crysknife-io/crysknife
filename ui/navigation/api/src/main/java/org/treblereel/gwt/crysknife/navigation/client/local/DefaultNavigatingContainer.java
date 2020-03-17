/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.treblereel.gwt.crysknife.navigation.client.local;

import jsinterop.base.Js;
import org.jboss.elemento.IsElement;

/**
 * Uses SimplePanel as a navigating container.
 *
 * @author Piotr Kosmowski
 */
public class DefaultNavigatingContainer implements NavigatingContainer {

    private final NavigationPanel panel = new NavigationPanel();

    private IsElement current;

    @Override
    public IsElement getWidget() {
        return current;
    }

    @Override
    public void clear() {
        for (int i = 0; i < panel.element().childNodes.length; i++) {
            panel.element().removeChild(panel.element().childNodes.getAt(i));
        }
    }

    @Override
    public void setWidget(IsElement w) {
        this.current = w;
        panel.element().appendChild(Js.uncheckedCast(w.element()));
    }

    public IsElement asWidget() {
        return panel;
    }

}
