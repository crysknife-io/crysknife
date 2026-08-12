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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class WebSocketFunctionalTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    int port;

    @BeforeEach
    void setUp() {
        TrackingServerEndpoint.clearEvents();
    }

    private URI wsUri(String path) {
        return URI.create("ws://localhost:" + port + path);
    }

    @Test
    void testTextEchoSendAndReceive() throws Exception {
        CompletableFuture<String> received = new CompletableFuture<>();

        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(wsUri("/echo"), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket,
                            CharSequence data, boolean last) {
                        received.complete(data.toString());
                        webSocket.request(1);
                        return null;
                    }
                })
                .join();

        ws.sendText("hello server", true).join();

        assertEquals("hello server", received.get(5, TimeUnit.SECONDS));

        ws.sendClose(WebSocket.NORMAL_CLOSURE, "done").join();
    }

    @Test
    void testBinaryEchoSendAndReceive() throws Exception {
        CompletableFuture<byte[]> received = new CompletableFuture<>();

        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(wsUri("/echo-binary"), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onBinary(WebSocket webSocket,
                            ByteBuffer data, boolean last) {
                        byte[] bytes = new byte[data.remaining()];
                        data.get(bytes);
                        received.complete(bytes);
                        webSocket.request(1);
                        return null;
                    }
                })
                .join();

        byte[] payload = new byte[]{1, 2, 3, 4, 5};
        ws.sendBinary(ByteBuffer.wrap(payload), true).join();

        assertArrayEquals(payload, received.get(5, TimeUnit.SECONDS));

        ws.sendClose(WebSocket.NORMAL_CLOSURE, "done").join();
    }

    @Test
    void testMultipleTextMessages() throws Exception {
        List<String> received = new CopyOnWriteArrayList<>();
        CompletableFuture<Void> allReceived = new CompletableFuture<>();

        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(wsUri("/echo"), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket,
                            CharSequence data, boolean last) {
                        received.add(data.toString());
                        if (received.size() == 3) {
                            allReceived.complete(null);
                        }
                        webSocket.request(1);
                        return null;
                    }
                })
                .join();

        ws.sendText("msg1", true).join();
        ws.sendText("msg2", true).join();
        ws.sendText("msg3", true).join();

        allReceived.get(5, TimeUnit.SECONDS);

        assertEquals(3, received.size());
        assertTrue(received.contains("msg1"));
        assertTrue(received.contains("msg2"));
        assertTrue(received.contains("msg3"));

        ws.sendClose(WebSocket.NORMAL_CLOSURE, "done").join();
    }

    @Test
    void testOnOpenAnnotationFires() throws Exception {
        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(wsUri("/tracking"), new WebSocket.Listener() {})
                .join();

        awaitEvent("OPEN");

        ws.sendClose(WebSocket.NORMAL_CLOSURE, "done").join();
    }

    @Test
    void testOnMessageAnnotationReceivesText() throws Exception {
        CompletableFuture<String> received = new CompletableFuture<>();

        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(wsUri("/tracking"), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket,
                            CharSequence data, boolean last) {
                        received.complete(data.toString());
                        webSocket.request(1);
                        return null;
                    }
                })
                .join();

        ws.sendText("test-payload", true).join();

        assertEquals("ECHO:test-payload", received.get(5, TimeUnit.SECONDS));

        awaitEvent("TEXT:test-payload");

        ws.sendClose(WebSocket.NORMAL_CLOSURE, "done").join();
    }

    @Test
    void testOnCloseAnnotationFires() throws Exception {
        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(wsUri("/tracking"), new WebSocket.Listener() {})
                .join();

        awaitEvent("OPEN");
        ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye").join();

        awaitEvent("CLOSE:");
    }

    @Test
    void testOnErrorAnnotationFires() throws Exception {
        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(wsUri("/tracking"), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket,
                            CharSequence data, boolean last) {
                        webSocket.request(1);
                        return null;
                    }
                })
                .join();

        ws.sendText("TRIGGER_ERROR", true).join();

        awaitEvent("ERROR:");
    }

    private void awaitEvent(String prefix) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            if (TrackingServerEndpoint.getEvents().stream()
                    .anyMatch(e -> e.startsWith(prefix))) {
                return;
            }
            Thread.sleep(100);
        }
        throw new AssertionError("Timed out waiting for event '" + prefix
                + "', got: " + TrackingServerEndpoint.getEvents());
    }
}
