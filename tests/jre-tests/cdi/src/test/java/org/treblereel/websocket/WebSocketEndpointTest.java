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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.gwt.websocket.client.WebSocketConfig;
import org.treblereel.gwt.websocket.client.transport.TransportListener;
import org.treblereel.gwt.websocket.client.transport.WebSocketTransport;

public class WebSocketEndpointTest extends AbstractTest {

    @Test
    public void testEndpointInjected() {
        assertNotNull(app.echoEndpointHolder);
        assertNotNull(app.echoEndpointHolder.echoEndpoint);
    }

    @Test
    public void testEndpointIsSingleton() {
        EchoEndpoint a = app.echoEndpointHolder.echoEndpoint;
        EchoEndpoint b = app.echoEndpointHolder.echoEndpoint;
        assertTrue(a == b);
    }

    @Test
    public void testOnOpenFires() {
        TestTransport transport = new TestTransport();
        EchoEndpoint endpoint = app.echoEndpointHolder.echoEndpoint;
        EchoEndpoint_WebSocketEndpoint proxy = new EchoEndpoint_WebSocketEndpoint(
                endpoint,
                WebSocketConfig.builder()
                        .url("ws://test")
                        .transport(transport)
                        .build());

        proxy.connect();
        transport.simulateOpen();
        assertTrue(endpoint.opened);
        assertNotNull(endpoint.session);
    }

    @Test
    public void testOnMessageFires() {
        TestTransport transport = new TestTransport();
        EchoEndpoint endpoint = app.echoEndpointHolder.echoEndpoint;
        EchoEndpoint_WebSocketEndpoint proxy = new EchoEndpoint_WebSocketEndpoint(
                endpoint,
                WebSocketConfig.builder()
                        .url("ws://test")
                        .transport(transport)
                        .build());

        proxy.connect();
        transport.simulateOpen();
        transport.simulateTextMessage("hello");
        assertEquals("hello", endpoint.lastMessage);
    }

    @Test
    public void testOnCloseFires() {
        TestTransport transport = new TestTransport();
        EchoEndpoint endpoint = app.echoEndpointHolder.echoEndpoint;
        EchoEndpoint_WebSocketEndpoint proxy = new EchoEndpoint_WebSocketEndpoint(
                endpoint,
                WebSocketConfig.builder()
                        .url("ws://test")
                        .transport(transport)
                        .build());

        proxy.connect();
        transport.simulateOpen();
        transport.simulateClose(1000, "Normal", true);
        assertNotNull(endpoint.lastCloseReason);
        assertEquals(1000, endpoint.lastCloseReason.getCloseCode().getCode());
    }

    @Test
    public void testOnErrorFires() {
        TestTransport transport = new TestTransport();
        EchoEndpoint endpoint = app.echoEndpointHolder.echoEndpoint;
        EchoEndpoint_WebSocketEndpoint proxy = new EchoEndpoint_WebSocketEndpoint(
                endpoint,
                WebSocketConfig.builder()
                        .url("ws://test")
                        .transport(transport)
                        .build());

        proxy.connect();
        transport.simulateOpen();
        transport.simulateError(new RuntimeException("fail"));
        assertNotNull(endpoint.lastError);
        assertEquals("fail", endpoint.lastError.getMessage());
    }

    @Test
    public void testDualMessageEndpointInjected() {
        assertNotNull(app.echoEndpointHolder.dualMessageEndpoint);
    }

    @Test
    public void testDualMessageTextFires() {
        TestTransport transport = new TestTransport();
        DualMessageEndpoint endpoint = app.echoEndpointHolder.dualMessageEndpoint;
        DualMessageEndpoint_WebSocketEndpoint proxy =
                new DualMessageEndpoint_WebSocketEndpoint(
                        endpoint,
                        WebSocketConfig.builder()
                                .url("ws://test")
                                .transport(transport)
                                .build());

        proxy.connect();
        transport.simulateOpen();
        transport.simulateTextMessage("text-payload");
        assertEquals("text-payload", endpoint.lastTextMessage);
    }

    @Test
    public void testDualMessageBinaryFires() {
        TestTransport transport = new TestTransport();
        DualMessageEndpoint endpoint = app.echoEndpointHolder.dualMessageEndpoint;
        DualMessageEndpoint_WebSocketEndpoint proxy =
                new DualMessageEndpoint_WebSocketEndpoint(
                        endpoint,
                        WebSocketConfig.builder()
                                .url("ws://test")
                                .transport(transport)
                                .build());

        proxy.connect();
        transport.simulateOpen();
        byte[] data = new byte[]{1, 2, 3};
        transport.simulateBinaryMessage(data);
        assertNotNull(endpoint.lastBinaryMessage);
        assertEquals(3, endpoint.lastBinaryMessage.length);
        assertEquals(1, endpoint.lastBinaryMessage[0]);
        assertEquals(2, endpoint.lastBinaryMessage[1]);
        assertEquals(3, endpoint.lastBinaryMessage[2]);
    }

    @Test
    public void testDualMessageBothWork() {
        TestTransport transport = new TestTransport();
        DualMessageEndpoint endpoint = app.echoEndpointHolder.dualMessageEndpoint;
        DualMessageEndpoint_WebSocketEndpoint proxy =
                new DualMessageEndpoint_WebSocketEndpoint(
                        endpoint,
                        WebSocketConfig.builder()
                                .url("ws://test")
                                .transport(transport)
                                .build());

        proxy.connect();
        transport.simulateOpen();

        transport.simulateTextMessage("hello");
        assertEquals("hello", endpoint.lastTextMessage);

        byte[] binary = new byte[]{10, 20};
        transport.simulateBinaryMessage(binary);
        assertNotNull(endpoint.lastBinaryMessage);
        assertEquals(10, endpoint.lastBinaryMessage[0]);

        assertEquals("hello", endpoint.lastTextMessage);
    }

    @Test
    public void testConnectReturnsVoid() {
        TestTransport transport = new TestTransport();
        EchoEndpoint endpoint = app.echoEndpointHolder.echoEndpoint;
        EchoEndpoint_WebSocketEndpoint proxy = new EchoEndpoint_WebSocketEndpoint(
                endpoint,
                WebSocketConfig.builder()
                        .url("ws://test")
                        .transport(transport)
                        .build());

        proxy.connect();
        transport.simulateOpen();
        assertTrue(endpoint.opened);
        assertNotNull(endpoint.session);
        endpoint.session.getBasicRemote().sendText("from-onopen");
        assertEquals("from-onopen", transport.lastSentMessage);
    }

    @Test
    public void testSendViaSession() {
        TestTransport transport = new TestTransport();
        EchoEndpoint endpoint = app.echoEndpointHolder.echoEndpoint;
        EchoEndpoint_WebSocketEndpoint proxy = new EchoEndpoint_WebSocketEndpoint(
                endpoint,
                WebSocketConfig.builder()
                        .url("ws://test")
                        .transport(transport)
                        .build());

        proxy.connect();
        transport.simulateOpen();
        Session session = proxy.getSession();
        session.getBasicRemote().sendText("outgoing");
        assertEquals("outgoing", transport.lastSentMessage);
    }

    static class TestTransport implements WebSocketTransport {
        TransportListener listener;
        String lastSentMessage;
        byte[] lastSentBinary;
        boolean open;

        @Override
        public void connect(String url, String[] subprotocols,
                Map<String, String> headers, TransportListener listener) {
            this.listener = listener;
        }

        @Override
        public void send(String message) {
            this.lastSentMessage = message;
        }

        @Override
        public void send(byte[] data) {
            this.lastSentBinary = data;
        }

        @Override
        public void close(int code, String reason) {
            this.open = false;
        }

        @Override
        public boolean isOpen() {
            return open;
        }

        void simulateOpen() {
            open = true;
            listener.onOpen();
        }

        void simulateTextMessage(String data) {
            listener.onTextMessage(data);
        }

        void simulateBinaryMessage(byte[] data) {
            listener.onBinaryMessage(data);
        }

        void simulateClose(int code, String reason, boolean wasClean) {
            open = false;
            listener.onClose(code, reason, wasClean);
        }

        void simulateError(Throwable error) {
            listener.onError(error);
        }
    }
}
