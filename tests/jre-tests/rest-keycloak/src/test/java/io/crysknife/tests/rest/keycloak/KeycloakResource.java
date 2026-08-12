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

import java.util.Map;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class KeycloakResource implements QuarkusTestResourceLifecycleManager {

    private KeycloakContainer keycloak;

    @Override
    public Map<String, String> start() {
        keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.0")
                .withRealmImportFile("test-realm.json");
        keycloak.start();

        String authServerUrl = keycloak.getAuthServerUrl() + "/realms/test-realm";

        return Map.of(
                "quarkus.oidc.auth-server-url", authServerUrl,
                "quarkus.oidc.client-id", "test-client",
                "keycloak.url", keycloak.getAuthServerUrl());
    }

    @Override
    public void stop() {
        if (keycloak != null) {
            keycloak.stop();
        }
    }
}
