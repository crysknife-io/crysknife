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

package org.treblereel.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class EmbeddedItemServer {

    private HttpServer server;
    private int port;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();

        server.createContext("/api/items", this::handleItems);

        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public String getBaseUrl() {
        return "http://localhost:" + port;
    }

    private void handleItems(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String subPath = path.substring("/api/items".length());

        switch (method) {
            case "GET":
                handleGet(exchange, subPath, uri);
                break;
            case "POST":
                handlePost(exchange, subPath);
                break;
            case "PUT":
                handlePut(exchange, subPath);
                break;
            case "DELETE":
                handleDelete(exchange, subPath);
                break;
            case "PATCH":
                handlePatch(exchange, subPath);
                break;
            default:
                respond(exchange, 405, "");
        }
    }

    private void handleGet(HttpExchange exchange, String subPath, URI uri) throws IOException {
        if (subPath.isEmpty() || subPath.equals("/")) {
            respondJson(exchange, 200,
                    "[{\"id\":1,\"name\":\"item-1\"},{\"id\":2,\"name\":\"item-2\"}]");
        } else if (subPath.equals("/search")) {
            Map<String, String> query = parseQueryParams(uri);
            String name = query.getOrDefault("name", "");
            respondJson(exchange, 200, "[{\"id\":1,\"name\":\"" + name + "\"}]");
        } else if (subPath.equals("/search-default")) {
            Map<String, String> query = parseQueryParams(uri);
            String page = query.getOrDefault("page", "0");
            respondJson(exchange, 200,
                    "[{\"id\":" + page + ",\"name\":\"page-" + page + "\"}]");
        } else if (subPath.equals("/auth-echo")) {
            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            if (auth == null) {
                auth = "none";
            }
            respondJson(exchange, 200, "{\"id\":0,\"name\":\"" + auth + "\"}");
        } else if (subPath.equals("/protected")) {
            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            if ("Bearer refreshed-token".equals(auth)) {
                respondJson(exchange, 200, "{\"id\":1,\"name\":\"protected-data\"}");
            } else {
                respondJson(exchange, 401, "{\"error\":\"unauthorized\"}");
            }
        } else if (subPath.equals("/error/404")) {
            respondJson(exchange, 404, "{\"error\":\"not found\"}");
        } else if (subPath.equals("/error/500")) {
            respondJson(exchange, 500, "{\"error\":\"internal server error\"}");
        } else if (subPath.matches("/\\d+/header")) {
            String id = subPath.split("/")[1];
            String headerValue = exchange.getRequestHeaders().getFirst("X-Custom-Header");
            if (headerValue == null) {
                headerValue = "none";
            }
            respondJson(exchange, 200,
                    "{\"id\":" + id + ",\"name\":\"" + headerValue + "\"}");
        } else if (subPath.matches("/\\d+/cookie")) {
            String id = subPath.split("/")[1];
            String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
            String sessionValue = "none";
            if (cookieHeader != null) {
                for (String part : cookieHeader.split(";")) {
                    String trimmed = part.trim();
                    if (trimmed.startsWith("session=")) {
                        sessionValue = trimmed.substring("session=".length());
                        break;
                    }
                }
            }
            respondJson(exchange, 200,
                    "{\"id\":" + id + ",\"name\":\"" + sessionValue + "\"}");
        } else if (subPath.matches("/\\d+/related/\\d+")) {
            String[] parts = subPath.substring(1).split("/");
            long a = Long.parseLong(parts[0]);
            long b = Long.parseLong(parts[2]);
            respondJson(exchange, 200,
                    "{\"id\":" + (a + b) + ",\"name\":\"related-" + a + "-" + b + "\"}");
        } else if (subPath.startsWith("/")) {
            String id = subPath.substring(1);
            respondJson(exchange, 200,
                    "{\"id\":" + id + ",\"name\":\"item-" + id + "\"}");
        } else {
            respond(exchange, 404, "");
        }
    }

    private void handlePost(HttpExchange exchange, String subPath) throws IOException {
        if (subPath.equals("/form")) {
            String body = readBody(exchange);
            Map<String, String> formParams = parseFormBody(body);
            String id = formParams.getOrDefault("id", "0");
            String name = formParams.getOrDefault("name", "");
            respondJson(exchange, 201,
                    "{\"id\":" + id + ",\"name\":\"" + name + "\"}");
        } else {
            String body = readBody(exchange);
            respondJson(exchange, 201, body);
        }
    }

    private void handlePut(HttpExchange exchange, String subPath) throws IOException {
        if (subPath.startsWith("/")) {
            String id = subPath.substring(1);
            String body = readBody(exchange);
            String updatedBody = body.replaceFirst("\"id\":\\d+", "\"id\":" + id);
            respondJson(exchange, 200, updatedBody);
        } else {
            respond(exchange, 404, "");
        }
    }

    private void handleDelete(HttpExchange exchange, String subPath) throws IOException {
        if (subPath.startsWith("/void/")) {
            respond(exchange, 204, "");
        } else if (subPath.startsWith("/")) {
            String id = subPath.substring(1);
            respondJson(exchange, 200,
                    "{\"id\":" + id + ",\"name\":\"deleted-" + id + "\"}");
        } else {
            respond(exchange, 404, "");
        }
    }

    private void handlePatch(HttpExchange exchange, String subPath) throws IOException {
        if (subPath.startsWith("/")) {
            String body = readBody(exchange);
            respondJson(exchange, 200, body);
        } else {
            respond(exchange, 404, "");
        }
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes("UTF-8");
        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private void respondJson(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        respond(exchange, status, body);
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        }
    }

    private Map<String, String> parseQueryParams(URI uri) {
        Map<String, String> params = new LinkedHashMap<>();
        String query = uri.getRawQuery();
        if (query == null || query.isEmpty()) {
            return params;
        }
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    private Map<String, String> parseFormBody(String body) {
        Map<String, String> params = new LinkedHashMap<>();
        if (body == null || body.isEmpty()) {
            return params;
        }
        for (String pair : body.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }
}
