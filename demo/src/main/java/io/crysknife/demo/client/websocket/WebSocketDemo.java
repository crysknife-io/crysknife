/*
 * Copyright © 2026 Treblereel
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

package io.crysknife.demo.client.websocket;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.websocket.client.WebSocketConnector;

@Singleton
@Page
@Templated("websocketdemo.html")
public class WebSocketDemo implements IsElement<HTMLDivElement> {

    @Inject
    WebSocketConnector<ChatEndpoint> connector;

    @Inject
    ChatEndpoint chatEndpoint;

    @Inject
    @DataField
    HTMLDivElement root;

    @Inject
    @DataField
    HTMLDivElement logArea;

    public void addLog(String message) {
        HTMLElement entry = (HTMLElement) DomGlobal.document.createElement("div");
        entry.textContent = message;
        logArea.appendChild(entry);
    }

    @Override
    public HTMLDivElement getElement() {
        return root;
    }
}
