/*
 * Copyright (C) 2026 treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.crysknife.tests.rest.keycloak.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.treblereel.gwt.rest.client.RestResponse;
import org.treblereel.gwt.rest.client.transport.RestRequest;
import org.treblereel.gwt.rest.client.transport.RestTransport;
import org.treblereel.gwt.rest.client.transport.TransportCallback;

public class JdkTransport implements RestTransport {

    @Override
    public void send(RestRequest request, TransportCallback callback) {
        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            sendViaHttpClient(request, callback);
            return;
        }
        try {
            URL url = new URL(request.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(request.getMethod());

            for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }

            if (request.getBody() != null && !request.getBody().isEmpty()) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(request.getBody().getBytes(StandardCharsets.UTF_8));
                }
            }

            int status = conn.getResponseCode();
            String statusText = conn.getResponseMessage();

            InputStream is = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
            String body = "";
            if (is != null) {
                body = readStream(is);
                is.close();
            }

            Map<String, String> responseHeaders = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
                if (entry.getKey() != null && !entry.getValue().isEmpty()) {
                    responseHeaders.put(entry.getKey(), entry.getValue().get(0));
                }
            }

            conn.disconnect();
            callback.onSuccess(new RestResponse(status, statusText, body, responseHeaders));
        } catch (IOException e) {
            callback.onError(e);
        }
    }

    private void sendViaHttpClient(RestRequest request, TransportCallback callback) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(request.getUrl()))
                    .method(request.getMethod(),
                            request.getBody() != null && !request.getBody().isEmpty()
                                    ? HttpRequest.BodyPublishers.ofString(request.getBody())
                                    : HttpRequest.BodyPublishers.noBody());

            for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }

            HttpResponse<String> resp = client.send(builder.build(),
                    HttpResponse.BodyHandlers.ofString());

            Map<String, String> responseHeaders = new HashMap<>();
            resp.headers().map().forEach((k, v) -> {
                if (!v.isEmpty()) {
                    responseHeaders.put(k, v.get(0));
                }
            });

            callback.onSuccess(new RestResponse(
                    resp.statusCode(), "", resp.body(), responseHeaders));
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    private String readStream(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }
}
