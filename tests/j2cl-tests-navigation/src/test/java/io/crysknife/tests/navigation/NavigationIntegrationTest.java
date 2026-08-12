/*
 * Copyright © 2026 Treblereel
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

package io.crysknife.tests.navigation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NavigationIntegrationTest {

    private static ChromeDriver driver;
    private static HttpServer server;
    private static String baseUrl;

    @BeforeAll
    static void setup() throws IOException {
        Path nested =
            Path.of("target/gwt/launcherDir/j2cl-tests-navigation/j2cl-tests-navigation")
                .toAbsolutePath();
        Path flat =
            Path.of("target/gwt/launcherDir/j2cl-tests-navigation").toAbsolutePath();
        Path webappDir = Files.exists(nested) ? nested : flat;
        assertTrue(Files.exists(webappDir),
            "J2CL output not found — run 'mvn clean install' first");

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }
            Path file = webappDir.resolve(path.substring(1));
            if (Files.exists(file) && !Files.isDirectory(file)) {
                byte[] data = Files.readAllBytes(file);
                String contentType = guessContentType(path);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(data);
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        });
        server.start();
        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--window-size=1920,1200");
        ChromeDriverService service = new ChromeDriverService.Builder()
            .withBuildCheckDisabled(true)
            .build();
        driver = new ChromeDriver(service, options);
    }

    @BeforeEach
    void loadPage() {
        driver.get(baseUrl);
        waitForElement("home-page");
    }

    // --- Default page ---

    @Test
    void testDefaultPageLoads() {
        assertNotNull(driver.findElement(By.id("home-page")));
        assertEquals("Home Page (DefaultPage)",
            driver.findElement(By.id("home-title")).getText());
    }

    @Test
    void testHomePageButtonsPresent() {
        assertNotNull(driver.findElement(By.id("go-to-a-btn")));
        assertNotNull(driver.findElement(By.id("go-to-b-btn")));
    }

    @Test
    void testNavigationContainerExists() {
        assertNotNull(driver.findElement(By.id("navigation-container")));
    }

    @Test
    void testLogDivExists() {
        assertNotNull(driver.findElement(By.id("log")));
    }

    // --- Navigation to PageA via button ---

    @Test
    void testNavigateToPageAViaButton() {
        clickElement("go-to-a-btn");
        waitForElement("page-a");
        assertEquals("Page A",
            driver.findElement(By.id("page-a-title")).getText());
    }

    @Test
    void testPageABackButtonPresent() {
        clickElement("go-to-a-btn");
        waitForElement("page-a");
        assertNotNull(driver.findElement(By.id("page-a-back-btn")));
    }

    @Test
    void testNavigateBackFromPageA() {
        clickElement("go-to-a-btn");
        waitForElement("page-a");

        clickElement("page-a-back-btn");
        waitForElement("home-page");
        assertEquals("Home Page (DefaultPage)",
            driver.findElement(By.id("home-title")).getText());
    }

    // --- Navigation to PageB with state ---

    @Test
    void testNavigateToPageBWithState() {
        clickElement("go-to-b-btn");
        waitForElement("page-b");
        assertEquals("Page B (State Parameters)",
            driver.findElement(By.id("page-b-title")).getText());
    }

    @Test
    void testPageStateFieldInjection() {
        clickElement("go-to-b-btn");
        waitForElement("page-b");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display")).getText();
                return text.contains("userId=123");
            });

        String stateText = driver.findElement(By.id("state-display")).getText();
        assertTrue(stateText.contains("userId=123"),
            "@PageState userId should be 123, got: " + stateText);
        assertTrue(stateText.contains("action=edit"),
            "@PageState action should be edit, got: " + stateText);
    }

    @Test
    void testPageStateDefaultValue() {
        clickElement("go-to-b-btn");
        waitForElement("page-b");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display")).getText();
                return text.contains("withDefault=");
            });

        String stateText = driver.findElement(By.id("state-display")).getText();
        assertTrue(stateText.contains("withDefault=fallback"),
            "@PageState defaultValue should be 'fallback', got: " + stateText);
    }

    @Test
    void testPageStateNullWhenAbsent() {
        clickElement("go-to-b-btn");
        waitForElement("page-b");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display")).getText();
                return text.contains("userId=123");
            });

        String stateText = driver.findElement(By.id("state-display")).getText();
        assertTrue(stateText.contains("key1=null"),
            "@PageState field should be null when param absent, got: " + stateText);
        assertTrue(stateText.contains("renamedField=null"),
            "@PageState renamedField should be null when param absent, got: " + stateText);
    }

    @Test
    void testPageStateRenamedParam() {
        navigateToHash("PageB?renamed_param=hello");
        waitForElement("page-b");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display")).getText();
                return text.contains("renamedField=");
            });

        String stateText = driver.findElement(By.id("state-display")).getText();
        assertTrue(stateText.contains("renamedField=hello"),
            "@PageState(value='renamed_param') should map to renamedField, got: " + stateText);
    }

    @Test
    void testPageStateViaHashWithParams() {
        navigateToHash("PageB?key1=value1&key2=value2");
        waitForElement("page-b");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display")).getText();
                return text.contains("key1=");
            });

        String stateText = driver.findElement(By.id("state-display")).getText();
        assertTrue(stateText.contains("key1=value1"),
            "@PageState key1 should be value1, got: " + stateText);
        assertTrue(stateText.contains("key2=value2"),
            "@PageState key2 should be value2, got: " + stateText);
    }

    @Test
    void testNavigateBackFromPageB() {
        clickElement("go-to-b-btn");
        waitForElement("page-b");

        clickElement("page-b-back-btn");
        waitForElement("home-page");
        assertNotNull(driver.findElement(By.id("home-title")));
    }

    // --- Hash-based navigation ---

    @Test
    void testHashNavigationToPageA() {
        navigateToHash("PageA");
        waitForElement("page-a");
        assertEquals("Page A",
            driver.findElement(By.id("page-a-title")).getText());
    }

    @Test
    void testHashNavigationToPageB() {
        navigateToHash("PageB");
        waitForElement("page-b");
        assertEquals("Page B (State Parameters)",
            driver.findElement(By.id("page-b-title")).getText());
    }

    @Test
    void testHashNavigationBackToHome() {
        navigateToHash("PageA");
        waitForElement("page-a");

        navigateToHash("HomePage");
        waitForElement("home-page");
        assertNotNull(driver.findElement(By.id("home-title")));
    }

    // --- Path parameters ---

    @Test
    void testPathParameterNavigation() {
        navigateToHash("items/42/detail");
        waitForElement("item-detail-page");
        assertEquals("Item Detail (Path Parameter)",
            driver.findElement(By.id("item-detail-title")).getText());
    }

    @Test
    void testPathParameterDisplaysValue() {
        navigateToHash("items/42/detail");
        waitForElement("item-detail-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("param-display")).getText();
                return text.contains("id");
            });

        String paramText =
            driver.findElement(By.id("param-display")).getText();
        assertTrue(paramText.contains("id"),
            "Expected 'id' in params, got: " + paramText);
        assertTrue(paramText.contains("42"),
            "Expected '42' in params, got: " + paramText);
    }

    @Test
    void testPathParameterBackButton() {
        navigateToHash("items/42/detail");
        waitForElement("item-detail-page");

        clickElement("item-detail-back-btn");
        waitForElement("home-page");
        assertNotNull(driver.findElement(By.id("home-title")));
    }

    // --- Lifecycle callbacks ---

    @Test
    void testHomePageLifecycleOnLoad() {
        String log = driver.findElement(By.id("log")).getText();
        assertTrue(log.contains("[HomePage] @PageShowing"),
            "Expected @PageShowing in log, got: " + log);
        assertTrue(log.contains("[HomePage] @PageShown"),
            "Expected @PageShown in log, got: " + log);
    }

    @Test
    void testLifecycleOrderOnNavigation() {
        clickElement("go-to-a-btn");
        waitForElement("page-a");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("[PageA] @PageShown"));

        String log = driver.findElement(By.id("log")).getText();
        int homeHiding = log.indexOf("[HomePage] @PageHiding");
        int homeHidden = log.indexOf("[HomePage] @PageHidden");
        int pageAShowing = log.indexOf("[PageA] @PageShowing");
        int pageAShown = log.indexOf("[PageA] @PageShown");

        assertTrue(homeHiding >= 0, "Missing [HomePage] @PageHiding");
        assertTrue(homeHidden >= 0, "Missing [HomePage] @PageHidden");
        assertTrue(pageAShowing >= 0, "Missing [PageA] @PageShowing");
        assertTrue(pageAShown >= 0, "Missing [PageA] @PageShown");

        assertTrue(homeHiding < pageAShowing,
            "HomePage @PageHiding should precede PageA @PageShowing");
        assertTrue(pageAShowing < pageAShown,
            "@PageShowing should precede @PageShown");
    }

    @Test
    void testLifecycleOnRoundTrip() {
        clickElement("go-to-a-btn");
        waitForElement("page-a");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("[PageA] @PageShown"));

        clickElement("page-a-back-btn");
        waitForElement("home-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("log")).getText();
                int count = countOccurrences(text, "[HomePage] @PageShown");
                return count >= 2;
            });

        String log = driver.findElement(By.id("log")).getText();
        assertTrue(log.contains("[PageA] @PageHiding"),
            "Missing [PageA] @PageHiding in round-trip");
        assertTrue(log.contains("[PageA] @PageHidden"),
            "Missing [PageA] @PageHidden in round-trip");
        assertTrue(countOccurrences(log, "[HomePage] @PageShowing") >= 2,
            "Expected at least 2 HomePage @PageShowing events");
    }

    // --- Sequential navigation ---

    @Test
    void testSequentialNavigation() {
        navigateToHash("PageA");
        waitForElement("page-a");

        navigateToHash("PageB");
        waitForElement("page-b");

        navigateToHash("items/99/detail");
        waitForElement("item-detail-page");

        navigateToHash("HomePage");
        waitForElement("home-page");
        assertNotNull(driver.findElement(By.id("home-title")));
    }

    // --- @PageState primitive types (PageC) ---

    @Test
    void testPageStatePrimitiveIntAndBoolean() {
        navigateToHash("PageC?count=7&active=true");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("count=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("count=7"),
            "@PageState int count should be 7, got: " + stateText);
        assertTrue(stateText.contains("active=true"),
            "@PageState boolean active should be true, got: " + stateText);
    }

    @Test
    void testPageStatePrimitiveLongAndDouble() {
        navigateToHash("PageC?bigNum=9999999&ratio=3.14");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("bigNum=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("bigNum=9999999"),
            "@PageState long bigNum should be 9999999, got: " + stateText);
        assertTrue(stateText.contains("ratio=3.14"),
            "@PageState double ratio should be 3.14, got: " + stateText);
    }

    @Test
    void testPageStatePrimitiveFloatShortByte() {
        navigateToHash("PageC?score=1.5&level=3&code=7");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("score=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("score=1.5"),
            "@PageState float score should be 1.5, got: " + stateText);
        assertTrue(stateText.contains("level=3"),
            "@PageState short level should be 3, got: " + stateText);
        assertTrue(stateText.contains("code=7"),
            "@PageState byte code should be 7, got: " + stateText);
    }

    @Test
    void testPageStatePrimitiveDefaults() {
        navigateToHash("PageC");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("countWithDefault=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("countWithDefault=42"),
            "@PageState int with defaultValue=42, got: " + stateText);
        assertTrue(stateText.contains("flagWithDefault=true"),
            "@PageState boolean with defaultValue=true, got: " + stateText);
        assertTrue(stateText.contains("longWithDefault=1000000"),
            "@PageState long with defaultValue=1000000, got: " + stateText);
        assertTrue(stateText.contains("doubleWithDefault=3.14"),
            "@PageState double with defaultValue=3.14, got: " + stateText);
        assertTrue(stateText.contains("floatWithDefault=1.5"),
            "@PageState float with defaultValue=1.5, got: " + stateText);
        assertTrue(stateText.contains("shortWithDefault=7"),
            "@PageState short with defaultValue=7, got: " + stateText);
        assertTrue(stateText.contains("byteWithDefault=3"),
            "@PageState byte with defaultValue=3, got: " + stateText);
    }

    @Test
    void testPageStatePrimitiveZeroDefaults() {
        navigateToHash("PageC");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("count=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("count=0"),
            "@PageState int without value should be 0 (Java default), got: " + stateText);
        assertTrue(stateText.contains("active=false"),
            "@PageState boolean without value should be false, got: " + stateText);
    }

    // --- @PageState boxed types (PageC) ---

    @Test
    void testPageStateBoxedTypesInjection() {
        navigateToHash("PageC?boxedInt=10&boxedLong=555&boxedBool=true&boxedDouble=2.71");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("boxedInt=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("boxedInt=10"),
            "@PageState Integer should be 10, got: " + stateText);
        assertTrue(stateText.contains("boxedLong=555"),
            "@PageState Long should be 555, got: " + stateText);
        assertTrue(stateText.contains("boxedBool=true"),
            "@PageState Boolean should be true, got: " + stateText);
        assertTrue(stateText.contains("boxedDouble=2.71"),
            "@PageState Double should be 2.71, got: " + stateText);
    }

    @Test
    void testPageStateBoxedNullWhenAbsent() {
        navigateToHash("PageC");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("boxedInt=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("boxedInt=null"),
            "@PageState Integer should be null when absent, got: " + stateText);
        assertTrue(stateText.contains("boxedLong=null"),
            "@PageState Long should be null when absent, got: " + stateText);
        assertTrue(stateText.contains("boxedBool=null"),
            "@PageState Boolean should be null when absent, got: " + stateText);
        assertTrue(stateText.contains("boxedDouble=null"),
            "@PageState Double should be null when absent, got: " + stateText);
    }

    @Test
    void testPageStateBoxedDefaultValue() {
        navigateToHash("PageC");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("boxedIntWithDefault=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("boxedIntWithDefault=99"),
            "@PageState Integer with defaultValue=99, got: " + stateText);
        assertTrue(stateText.contains("boxedLongWithDefault=500"),
            "@PageState Long with defaultValue=500, got: " + stateText);
        assertTrue(stateText.contains("boxedBoolWithDefault=false"),
            "@PageState Boolean with defaultValue=false, got: " + stateText);
        assertTrue(stateText.contains("boxedDoubleWithDefault=2.72"),
            "@PageState Double with defaultValue=2.72, got: " + stateText);
    }

    @Test
    void testPageStateListOfStrings() {
        navigateToHash("PageC?tags=alpha&tags=beta&tags=gamma");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("tags=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("tags=[alpha, beta, gamma]"),
            "@PageState List<String> tags should be [alpha, beta, gamma], got: " + stateText);
    }

    @Test
    void testPageStateListOfIntegers() {
        navigateToHash("PageC?ids=10&ids=20&ids=30");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("ids=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("ids=[10, 20, 30]"),
            "@PageState List<Integer> ids should be [10, 20, 30], got: " + stateText);
    }

    @Test
    void testPageStateListEmptyWhenAbsent() {
        navigateToHash("PageC");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("tags=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("tags=[]"),
            "@PageState List<String> should be empty when absent, got: " + stateText);
        assertTrue(stateText.contains("ids=[]"),
            "@PageState List<Integer> should be empty when absent, got: " + stateText);
        assertTrue(stateText.contains("longList=[]"),
            "@PageState List<Long> should be empty when absent, got: " + stateText);
        assertTrue(stateText.contains("flags=[]"),
            "@PageState List<Boolean> should be empty when absent, got: " + stateText);
    }

    @Test
    void testPageStateListOfMixedTypes() {
        navigateToHash("PageC?longList=100&longList=200&flags=true&flags=false");
        waitForElement("page-c");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> {
                String text = d.findElement(By.id("state-display-c")).getText();
                return text.contains("longList=");
            });

        String stateText = driver.findElement(By.id("state-display-c")).getText();
        assertTrue(stateText.contains("longList=[100, 200]"),
            "@PageState List<Long> should be [100, 200], got: " + stateText);
        assertTrue(stateText.contains("flags=[true, false]"),
            "@PageState List<Boolean> should be [true, false], got: " + stateText);
    }

    // --- Listener dedup after reinit ---

    @Test
    void testNoDoubleEventsAfterReinit() {
        clickElement("go-to-a-btn");
        waitForElement("page-a");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("[PageA] @PageShown"));

        clickElement("page-a-back-btn");
        waitForElement("home-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> countOccurrences(
                d.findElement(By.id("log")).getText(), "[HomePage] @PageShown") >= 2);

        // Re-initialize navigation (calls setNavigationContainer again → init())
        clickElement("reinit-nav-btn");

        // Clear the log so we can count fresh events
        ((JavascriptExecutor) driver)
            .executeScript("document.getElementById('log').textContent = ''");

        // Navigate to PageA via hash
        navigateToHash("PageA");
        waitForElement("page-a");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("[PageA] @PageShown"));

        String log = driver.findElement(By.id("log")).getText();
        assertEquals(1, countOccurrences(log, "[PageA] @PageShowing"),
            "After reinit, @PageShowing should fire exactly once, log: " + log);
        assertEquals(1, countOccurrences(log, "[PageA] @PageShown"),
            "After reinit, @PageShown should fire exactly once, log: " + log);
    }

    @Test
    void testMultipleReinitStillSingleEvents() {
        // Reinit navigation 3 times
        clickElement("reinit-nav-btn");
        clickElement("reinit-nav-btn");
        clickElement("reinit-nav-btn");

        // Clear log
        ((JavascriptExecutor) driver)
            .executeScript("document.getElementById('log').textContent = ''");

        navigateToHash("PageA");
        waitForElement("page-a");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("[PageA] @PageShown"));

        String log = driver.findElement(By.id("log")).getText();
        assertEquals(1, countOccurrences(log, "[PageA] @PageShowing"),
            "After 3 reinits, @PageShowing should fire exactly once, log: " + log);
        assertEquals(1, countOccurrences(log, "[PageA] @PageShown"),
            "After 3 reinits, @PageShown should fire exactly once, log: " + log);
    }

    // --- helpers ---

    private void clickElement(String id) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(By.id(id)));
        ((JavascriptExecutor) driver)
            .executeScript("document.getElementById('" + id + "').click()");
    }

    private void navigateToHash(String hash) {
        ((JavascriptExecutor) driver)
            .executeScript("window.location.hash = '#" + hash + "'");
    }

    private void waitForElement(String id) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(pattern, idx)) != -1) {
            count++;
            idx += pattern.length();
        }
        return count;
    }

    private static String guessContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "application/octet-stream";
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (server != null) {
            server.stop(0);
        }
    }
}
