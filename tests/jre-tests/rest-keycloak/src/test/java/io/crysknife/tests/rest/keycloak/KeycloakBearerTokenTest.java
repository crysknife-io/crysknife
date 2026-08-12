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

package io.crysknife.tests.rest.keycloak;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import io.crysknife.tests.rest.keycloak.model.SecureItem;
import io.crysknife.tests.rest.keycloak.service.SecureItemService;
import io.crysknife.tests.rest.keycloak.service.SecureItemService_RestCaller;
import io.crysknife.tests.rest.keycloak.transport.JdkTransport;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.treblereel.gwt.rest.client.Caller;
import org.treblereel.gwt.rest.client.RestConfig;
import org.treblereel.gwt.rest.client.RestResponse;
import org.treblereel.gwt.rest.client.RetryPolicy;
import org.treblereel.gwt.rest.client.proxy.UrlBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
@QuarkusTestResource(KeycloakResource.class)
public class KeycloakBearerTokenTest {

    @TestHTTPResource("/")
    URL baseUrl;

    @ConfigProperty(name = "keycloak.url")
    String keycloakBaseUrl;

    private String tokenEndpoint;

    @BeforeEach
    void setUp() {
        UrlBuilder.setEncoder(value -> value);
        tokenEndpoint = keycloakBaseUrl
                + "/realms/test-realm/protocol/openid-connect/token";
    }

    private Caller<SecureItemService> callerWith(RestConfig config) {
        return new SecureItemService_RestCaller(config);
    }

    private RestConfig.Builder baseConfigBuilder() {
        return RestConfig.builder()
                .baseUrl(baseUrl.toString())
                .transport(new JdkTransport());
    }

    @Test
    void testValidTokenReturnsProtectedData() {
        String token = getTokenFromKeycloak("testuser", "testpassword");
        assertNotNull(token, "Token acquisition failed");

        Caller<SecureItemService> caller = callerWith(
                baseConfigBuilder().bearerToken(token).build());

        SecureItem[] result = new SecureItem[1];
        caller.call(r -> result[0] = (SecureItem) r).getSecureItem();

        assertNotNull(result[0]);
        assertEquals(1, result[0].getId());
        assertEquals("protected-data", result[0].getName());
    }

    @Test
    void testNoTokenReturns401() {
        Caller<SecureItemService> caller = callerWith(
                baseConfigBuilder().build());

        RestResponse[] errorResponse = new RestResponse[1];
        SecureItem[] successResult = new SecureItem[1];
        caller.onError((response, throwable) -> errorResponse[0] = response)
                .call(r -> successResult[0] = (SecureItem) r)
                .getSecureItem();

        assertNotNull(errorResponse[0]);
        assertEquals(401, errorResponse[0].getStatusCode());
        assertNull(successResult[0]);
    }

    @Test
    void testExpiredTokenRetryWithRefreshSucceeds() throws InterruptedException {
        String initialToken = getTokenFromKeycloak("testuser", "testpassword");
        assertNotNull(initialToken, "Initial token acquisition failed");

        Thread.sleep(6000);

        String[] currentToken = {initialToken};

        Caller<SecureItemService> caller = callerWith(
                baseConfigBuilder()
                        .bearerToken(() -> currentToken[0])
                        .retryPolicy(RetryPolicy.builder()
                                .maxRetries(1)
                                .delayMs(0)
                                .condition((response, error, attempt) ->
                                        response != null
                                                && response.getStatusCode() == 401)
                                .build())
                        .responseFilter((reqCtx, respCtx) -> {
                            if (respCtx.getStatusCode() == 401) {
                                currentToken[0] = getTokenFromKeycloak(
                                        "testuser", "testpassword");
                            }
                        })
                        .build());

        SecureItem[] result = new SecureItem[1];
        RestResponse[] errorResponse = new RestResponse[1];
        caller.onError((resp, t) -> errorResponse[0] = resp)
                .call(r -> result[0] = (SecureItem) r)
                .getSecureItem();

        assertNotNull(result[0], "Retry should have succeeded with fresh token");
        assertEquals(1, result[0].getId());
        assertEquals("protected-data", result[0].getName());
        assertNull(errorResponse[0], "No error expected after successful retry");
    }

    @Test
    void testDynamicBearerTokenSupplierPerRequest() {
        String token1 = getTokenFromKeycloak("testuser", "testpassword");
        String token2 = getTokenFromKeycloak("testuser", "testpassword");
        assertNotNull(token1);
        assertNotNull(token2);

        String[] currentToken = {token1};

        Caller<SecureItemService> caller = callerWith(
                baseConfigBuilder()
                        .bearerToken(() -> currentToken[0])
                        .build());

        SecureItem[] result1 = new SecureItem[1];
        caller.call(r -> result1[0] = (SecureItem) r).getSecureItem();
        assertNotNull(result1[0]);
        assertEquals("protected-data", result1[0].getName());

        currentToken[0] = token2;

        SecureItem[] result2 = new SecureItem[1];
        caller.call(r -> result2[0] = (SecureItem) r).getSecureItem();
        assertNotNull(result2[0]);
        assertEquals("protected-data", result2[0].getName());
    }

    private String getTokenFromKeycloak(String username, String password) {
        try {
            URL url = new URL(tokenEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            String body = "grant_type=password"
                    + "&client_id=test-client"
                    + "&username=" + username
                    + "&password=" + password;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            if (status != 200) {
                return null;
            }

            String responseBody;
            try (InputStream is = conn.getInputStream()) {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                responseBody = result.toString(StandardCharsets.UTF_8);
            }

            conn.disconnect();

            int tokenStart = responseBody.indexOf("\"access_token\":\"") + 16;
            int tokenEnd = responseBody.indexOf("\"", tokenStart);
            return responseBody.substring(tokenStart, tokenEnd);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get token from Keycloak", e);
        }
    }
}
