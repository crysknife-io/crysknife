/*
 * Copyright © 2024 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.crysknife.ui.websocket.client;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.treblereel.gwt.websocket.client.WebSocketConfig;
import org.treblereel.gwt.websocket.client.proxy.AbstractWebSocketEndpoint;

public class BrowserWebSocketConnector<T> implements WebSocketConnector<T> {

    private final Supplier<T> delegateFactory;
    private final BiFunction<T, WebSocketConfig, AbstractWebSocketEndpoint> endpointFactory;
    private String uri;
    private String subprotocol;

    public BrowserWebSocketConnector(
            Supplier<T> delegateFactory,
            BiFunction<T, WebSocketConfig, AbstractWebSocketEndpoint> endpointFactory) {
        this.delegateFactory = delegateFactory;
        this.endpointFactory = endpointFactory;
    }

    @Override
    public WebSocketConnector<T> baseUri(String uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public WebSocketConnector<T> subprotocol(String subprotocol) {
        this.subprotocol = subprotocol;
        return this;
    }

    @Override
    public void connect() {
        WebSocketConfig.Builder builder = WebSocketConfig.builder().url(uri);
        if (subprotocol != null) {
            builder.subprotocols(subprotocol);
        }
        WebSocketConfig config = builder.build();
        T delegate = delegateFactory.get();
        AbstractWebSocketEndpoint endpoint = endpointFactory.apply(delegate, config);
        endpoint.connect();
    }
}
