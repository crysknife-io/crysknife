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

import jakarta.inject.Singleton;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

@ClientEndpoint
@Singleton
public class EchoEndpoint {

    private WebSocketTestPanel panel;

    public void setPanel(WebSocketTestPanel panel) {
        this.panel = panel;
    }

    @OnOpen
    public void onOpen(Session session) {
        if (panel != null) {
            panel.addStatus("CONNECTED");
            session.getBasicRemote().sendText("hello");
        }
    }

    @OnMessage
    public void onMessage(String message) {
        if (panel != null) {
            panel.addMessage(message);
        }
    }

    @OnClose
    public void onClose(CloseReason reason) {
        if (panel != null) {
            panel.addStatus("CLOSED:" + reason.getCloseCode().getCode());
        }
    }

    @OnError
    public void onError(Throwable error) {
        if (panel != null) {
            panel.addStatus("ERROR:" + error.getMessage());
        }
    }
}
