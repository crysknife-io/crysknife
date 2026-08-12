/*
 * Copyright © 2024 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.crysknife.tests.websocket.client;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import io.crysknife.ui.websocket.client.WebSocketConnector;

@Singleton
@Templated
public class WebSocketTestPanel implements IsElement<HTMLDivElement> {

    @Inject
    WebSocketConnector<EchoEndpoint> connector;

    @Inject
    EchoEndpoint echoEndpoint;

    @DataField
    HTMLDivElement statusDiv;

    @DataField
    HTMLDivElement messagesDiv;

    @PostConstruct
    public void init() {
        echoEndpoint.setPanel(this);

        String wsUrl = "ws://" + DomGlobal.window.location.hostname + ":"
                + DomGlobal.window.location.port + "/echo";

        connector.baseUri(wsUrl).connect();

        DomGlobal.document.body.appendChild(getElement());
    }

    public void addStatus(String status) {
        statusDiv.textContent = status;
    }

    public void addMessage(String message) {
        HTMLDivElement msgDiv = (HTMLDivElement) DomGlobal.document.createElement("div");
        msgDiv.className = "message";
        msgDiv.textContent = message;
        messagesDiv.appendChild(msgDiv);
    }
}
