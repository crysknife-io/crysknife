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
public class ChatEndpoint {

    private WebSocketDemo panel;

    public void setPanel(WebSocketDemo panel) {
        this.panel = panel;
    }

    @OnOpen
    public void onOpen(Session session) {
        if (panel != null) {
            panel.addLog("Connected to server");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (panel != null) {
            panel.addLog("Received: " + message);
        }
    }

    @OnClose
    public void onClose(CloseReason reason) {
        if (panel != null) {
            panel.addLog("Disconnected: " + reason.getCloseCode().getCode());
        }
    }

    @OnError
    public void onError(Throwable error) {
        if (panel != null) {
            panel.addLog("Error: " + error.getMessage());
        }
    }
}
