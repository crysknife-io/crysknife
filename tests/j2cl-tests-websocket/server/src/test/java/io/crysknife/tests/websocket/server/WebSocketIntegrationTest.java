/*
 * Copyright © 2024 Treblereel
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

import java.time.Duration;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class WebSocketIntegrationTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    int port;

    private static WebDriver driver;

    @BeforeAll
    static void setupDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    static void teardownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testEchoMessage() {
        driver.get("http://localhost:" + port + "/index.html");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement statusDiv = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("statusDiv")));
        wait.until(d -> statusDiv.getText().contains("CONNECTED"));
        assertTrue(statusDiv.getText().contains("CONNECTED"));

        WebElement messagesDiv = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("messagesDiv")));
        wait.until(d -> !messagesDiv.getText().isEmpty());
        assertTrue(messagesDiv.getText().contains("hello"));
    }
}
