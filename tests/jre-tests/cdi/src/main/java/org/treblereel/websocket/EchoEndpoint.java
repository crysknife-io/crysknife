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

package org.treblereel.websocket;

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

    public boolean opened;
    public Session session;
    public String lastMessage;
    public CloseReason lastCloseReason;
    public Throwable lastError;

    @OnOpen
    public void onOpen(Session session) {
        this.opened = true;
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        this.lastMessage = message;
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        this.lastCloseReason = closeReason;
    }

    @OnError
    public void onError(Throwable error) {
        this.lastError = error;
    }
}
