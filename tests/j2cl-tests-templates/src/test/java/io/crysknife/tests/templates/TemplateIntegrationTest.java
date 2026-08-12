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

package io.crysknife.tests.templates;

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

public class TemplateIntegrationTest {

    private static ChromeDriver driver;
    private static HttpServer server;
    private static String baseUrl;

    @BeforeAll
    static void setup() throws IOException {
        Path nested =
            Path.of("target/gwt/launcherDir/j2cl-tests-templates/j2cl-tests-templates")
                .toAbsolutePath();
        Path flat =
            Path.of("target/gwt/launcherDir/j2cl-tests-templates").toAbsolutePath();
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
        try {
            waitForElement("basic-page");
        } catch (Exception e) {
            String src = driver.getPageSource();
            String tc = (String) ((JavascriptExecutor) driver).executeScript(
                "return document.getElementById('test-container') "
                    + "? document.getElementById('test-container').innerHTML : 'NOT FOUND'");
            String errors = (String) ((JavascriptExecutor) driver).executeScript(
                "return window.__jsErrors ? window.__jsErrors.join('\\n') : 'no error capture'");
            throw new RuntimeException(
                "basic-page not found.\ntest-container: " + tc
                    + "\npage source length: " + src.length()
                    + "\nJS errors: " + errors
                    + "\npage source (first 1000): " + src.substring(0, Math.min(1000, src.length())),
                e);
        }
        clearLog();
    }

    // ==================== Basic Templates ====================

    @Test
    void testBasicTemplateRenders() {
        assertNotNull(driver.findElement(By.id("basic-page")));
        assertEquals("Basic Template Test",
            driver.findElement(By.id("basic-title")).getText());
    }

    @Test
    void testBasicTemplateDataFieldSpan() {
        assertEquals("Default title text",
            driver.findElement(By.id("basic-span")).getText());
    }

    @Test
    void testRootElementAttributesFromTemplate() {
        String cls = driver.findElement(By.id("basic-page")).getAttribute("class");
        assertTrue(cls != null && cls.contains("basic-root-class"),
            "Root element should have class from template, got: " + cls);
    }

    @Test
    void testExplicitPathTemplateRenders() {
        navigateToHash("ExplicitPath");
        waitForElement("explicit-path-page");
        assertEquals("Explicit Path Template",
            driver.findElement(By.id("explicit-path-title")).getText());
    }

    @Test
    void testInlineTemplateRenders() {
        navigateToHash("InlinePage");
        waitForElement("inline-page");
        assertEquals("Inline Template",
            driver.findElement(By.id("inline-title")).getText());
    }

    @Test
    void testInlineTemplateDataField() {
        navigateToHash("InlinePage");
        waitForElement("inline-page");
        assertEquals("Inline content here",
            driver.findElement(By.id("inline-content")).getText());
    }

    @Test
    void testFragmentSelectorRenders() {
        navigateToHash("FragmentPage");
        waitForElement("fragment-page");
        assertEquals("Fragment Template (section1)",
            driver.findElement(By.id("fragment-title")).getText());
    }

    @Test
    void testFragmentSelectorDoesNotRenderOtherSection() {
        navigateToHash("FragmentPage");
        waitForElement("fragment-page");
        assertTrue(driver.findElements(By.id("fragment-other")).isEmpty(),
            "section2 should NOT be rendered");
    }

    // ==================== DataField ====================

    @Test
    void testDataFieldDefaultName() {
        navigateToHash("DataFieldPage");
        waitForElement("datafield-page");
        assertEquals("initial",
            driver.findElement(By.id("datafield-info")).getText());
    }

    @Test
    void testDataFieldExplicitAlias() {
        navigateToHash("DataFieldPage");
        waitForElement("datafield-page");
        assertEquals("Renamed field content",
            driver.findElement(By.id("datafield-renamed")).getText());
    }

    @Test
    void testDataFieldInputInteraction() {
        navigateToHash("DataFieldPage");
        waitForElement("datafield-page");

        ((JavascriptExecutor) driver).executeScript(
            "document.getElementById('datafield-input').value = 'Hello Test'");
        clickElement("datafield-action-btn");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("datafield-info")).getText().equals("Hello Test"));

        assertEquals("Hello Test",
            driver.findElement(By.id("datafield-info")).getText());
    }

    @Test
    void testDataFieldPreInitialized() {
        navigateToHash("DataFieldInitialized");
        waitForElement("datafield-init-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> !d.findElements(By.id("initialized-banner")).isEmpty());

        assertEquals("Java-created banner",
            driver.findElement(By.id("initialized-banner")).getText());
    }

    @Test
    void testUseBeanPreservesTemplateOnlyAttributes() {
        navigateToHash("DataFieldInitialized");
        waitForElement("datafield-init-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> !d.findElements(By.id("initialized-banner")).isEmpty());

        String cls = driver.findElement(By.id("initialized-banner"))
            .getAttribute("class");
        assertTrue(cls != null && cls.contains("original-class"),
            "Template-only class should be copied to bean element, got: " + cls);

        String dataField = driver.findElement(By.id("initialized-banner"))
            .getAttribute("data-field");
        assertEquals("banner", dataField,
            "Template data-field attribute should be copied to bean element");
    }

    @Test
    void testUseBeanEmptyContentPreservesTemplateContent() {
        navigateToHash("MixedStrategy");
        waitForElement("mixed-strategy-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> !d.findElements(By.id("bean-empty-div")).isEmpty());

        assertEquals("Template content for empty bean",
            driver.findElement(By.id("bean-empty-div")).getText(),
            "USE_BEAN with empty bean content should preserve template content");
    }

    @Test
    void testMixedStrategiesOnSamePage() {
        navigateToHash("MixedStrategy");
        waitForElement("mixed-strategy-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> !d.findElements(By.id("bean-empty-div")).isEmpty());

        assertNotNull(driver.findElement(By.id("bean-empty-div")),
            "USE_BEAN field should keep bean id");

        String templateWinsId = (String) ((JavascriptExecutor) driver).executeScript(
            "var el = document.querySelector('[data-field=templateWins]');"
            + "return el ? el.id : null;");
        assertEquals("tpl-wins", templateWinsId,
            "USE_TEMPLATE field should have template id, not bean id");
    }

    // ==================== EventHandler ====================

    @Test
    void testEventHandlerSingleClick() {
        navigateToHash("EventHandlerPage");
        waitForElement("event-page");

        clickElement("event-single-btn");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("event-result")).getText().equals("single-click"));

        assertEquals("single-click",
            driver.findElement(By.id("event-result")).getText());
    }

    @Test
    void testEventHandlerMultipleTargetA() {
        navigateToHash("EventHandlerPage");
        waitForElement("event-page");

        clickElement("event-btn-a");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("event-result")).getText()
                .contains("multi-target:event-btn-a"));

        assertTrue(driver.findElement(By.id("event-result")).getText()
            .contains("multi-target:event-btn-a"));
    }

    @Test
    void testEventHandlerMultipleTargetB() {
        navigateToHash("EventHandlerPage");
        waitForElement("event-page");

        clickElement("event-btn-b");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("event-result")).getText()
                .contains("multi-target:event-btn-b"));

        assertTrue(driver.findElement(By.id("event-result")).getText()
            .contains("multi-target:event-btn-b"));
    }

    @Test
    void testEventHandlerMultipleEventTypes() {
        navigateToHash("EventHandlerPage");
        waitForElement("event-page");

        ((JavascriptExecutor) driver).executeScript(
            "var btn = document.getElementById('event-multi-btn');"
                + "btn.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));"
                + "btn.dispatchEvent(new MouseEvent('mouseup', {bubbles: true}));");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("event-result")).getText()
                .contains("multi-event:mouseup"));

        String log = driver.findElement(By.id("log")).getText();
        assertTrue(log.contains("multi-event:mousedown"),
            "mousedown should have fired, log: " + log);
        assertTrue(log.contains("multi-event:mouseup"),
            "mouseup should have fired, log: " + log);
    }

    @Test
    void testEventHandlerRootElement() {
        navigateToHash("EventHandlerPage");
        waitForElement("event-page");

        clickElement("event-single-btn");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("root-click"));

        String log = driver.findElement(By.id("log")).getText();
        assertTrue(log.contains("root-click"),
            "Root click handler should have fired, log: " + log);
    }

    // ==================== Composition ====================

    @Test
    void testNestedTemplatedComponent() {
        navigateToHash("CompositionPage");
        waitForElement("composition-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> !d.findElements(By.id("child-content")).isEmpty());

        assertNotNull(driver.findElement(By.id("child-content")));
    }

    @Test
    void testChildComponentHasOwnMarkup() {
        navigateToHash("CompositionPage");
        waitForElement("composition-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> !d.findElements(By.id("child-content")).isEmpty());

        assertEquals("Child component rendered",
            driver.findElement(By.id("child-content")).getText());
    }

    @Test
    void testDependentChildComposition() {
        navigateToHash("DependentComposition");
        waitForElement("dependent-composition-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> !d.findElements(By.id("dependent-child-content")).isEmpty());

        assertEquals("Dependent child rendered",
            driver.findElement(By.id("dependent-child-content")).getText());
    }

    @Test
    void testEventHandlerOnIsElementChild() {
        navigateToHash("EventOnChild");
        waitForElement("event-on-child-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> !d.findElements(By.id("child-content")).isEmpty());

        clickElement("child-content");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("child-clicked"));

        assertTrue(driver.findElement(By.id("log")).getText()
            .contains("child-clicked"),
            "EventHandler on IsElement DataField should fire on click");
    }

    // ==================== Styles ====================

    @Test
    void testAutoDiscoveredStylesheet() {
        navigateToHash("AutoStylePage");
        waitForElement("auto-style-page");

        String color = (String) ((JavascriptExecutor) driver).executeScript(
            "return getComputedStyle(document.getElementById('auto-styled-element')).color");

        assertEquals("rgb(0, 128, 0)", color,
            "Auto-discovered CSS should set color to green");
    }

    @Test
    void testExplicitStylesheet() {
        navigateToHash("ExplicitStylePage");
        waitForElement("explicit-style-page");

        String borderWidth = (String) ((JavascriptExecutor) driver).executeScript(
            "return getComputedStyle(document.getElementById('explicit-styled-element'))"
                + ".borderTopWidth");

        assertEquals("2px", borderWidth,
            "Explicit stylesheet should set border-width to 2px");
    }

    // ==================== Lifecycle ====================

    @Test
    void testPostConstructFires() {
        navigateToHash("LifecyclePage");
        waitForElement("lifecycle-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("[LifecyclePage] @PostConstruct"));

        String log = driver.findElement(By.id("log")).getText();
        assertTrue(log.contains("[LifecyclePage] @PostConstruct"),
            "Expected @PostConstruct in log, got: " + log);
    }

    @Test
    void testOnAttachFires() {
        navigateToHash("LifecyclePage");
        waitForElement("lifecycle-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("[LifecyclePage] @OnAttach"));

        String log = driver.findElement(By.id("log")).getText();
        assertTrue(log.contains("[LifecyclePage] @OnAttach"),
            "Expected @OnAttach in log, got: " + log);
    }

    @Test
    void testOnDetachFiresOnNavAway() {
        navigateToHash("LifecyclePage");
        waitForElement("lifecycle-page");

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("[LifecyclePage] @OnAttach"));

        navigateToHash("BasicTemplatePage");
        waitForElement("basic-page");

        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(d -> d.findElement(By.id("log")).getText()
                .contains("[LifecyclePage] @OnDetach"));

        String log = driver.findElement(By.id("log")).getText();
        assertTrue(log.contains("[LifecyclePage] @OnDetach"),
            "Expected @OnDetach in log after navigating away, got: " + log);
    }

    // ==================== Helpers ====================

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

    private void clearLog() {
        ((JavascriptExecutor) driver)
            .executeScript("var el = document.getElementById('log'); if(el) el.textContent = ''");
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
