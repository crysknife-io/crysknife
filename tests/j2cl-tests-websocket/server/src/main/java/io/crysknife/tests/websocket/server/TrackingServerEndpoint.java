/*
 * Copyright © 2026 Treblereel
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
package io.crysknife.tests.websocket.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/tracking")
public class TrackingServerEndpoint {

    private static final List<String> events = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(Session session) {
        events.add("OPEN");
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        events.add("TEXT:" + message);
        if ("TRIGGER_ERROR".equals(message)) {
            throw new RuntimeException("Intentional test error");
        }
        return "ECHO:" + message;
    }

    @OnClose
    public void onClose(CloseReason reason) {
        events.add("CLOSE:" + reason.getCloseCode().getCode());
    }

    @OnError
    public void onError(Throwable error) {
        events.add("ERROR:" + error.getMessage());
    }

    public static List<String> getEvents() {
        return new ArrayList<>(events);
    }

    public static void clearEvents() {
        events.clear();
    }
}
