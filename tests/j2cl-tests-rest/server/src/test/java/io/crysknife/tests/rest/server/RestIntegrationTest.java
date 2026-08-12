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

package io.crysknife.tests.rest.server;

import java.net.URL;
import java.time.Duration;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class RestIntegrationTest {

    private static ChromeDriver driver;

    @TestHTTPResource("/")
    URL baseUrl;

    @BeforeAll
    static void setupDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--window-size=1920,1200");
        driver = new ChromeDriver(options);
    }

    @BeforeEach
    void loadPage() {
        driver.get(baseUrl.toString());
    }

    // --- Page structure ---

    @Test
    void testPageTitle() {
        assertEquals("REST Caller J2CL Test", driver.getTitle());
    }

    @Test
    void testAllButtonsPresent() {
        assertNotNull(driver.findElement(By.id("get-item-btn")));
        assertNotNull(driver.findElement(By.id("list-items-btn")));
        assertNotNull(driver.findElement(By.id("create-item-btn")));
        assertNotNull(driver.findElement(By.id("update-item-btn")));
        assertNotNull(driver.findElement(By.id("delete-item-btn")));
        assertNotNull(driver.findElement(By.id("search-items-btn")));
    }

    @Test
    void testAllResultDivsPresent() {
        assertNotNull(driver.findElement(By.id("get-item-result")));
        assertNotNull(driver.findElement(By.id("list-items-result")));
        assertNotNull(driver.findElement(By.id("create-item-result")));
        assertNotNull(driver.findElement(By.id("update-item-result")));
        assertNotNull(driver.findElement(By.id("delete-item-result")));
        assertNotNull(driver.findElement(By.id("search-items-result")));
    }

    @Test
    void testResultDivsStartEmpty() {
        assertEquals("", driver.findElement(By.id("get-item-result")).getText());
        assertEquals("", driver.findElement(By.id("list-items-result")).getText());
        assertEquals("", driver.findElement(By.id("create-item-result")).getText());
        assertEquals("", driver.findElement(By.id("update-item-result")).getText());
        assertEquals("", driver.findElement(By.id("delete-item-result")).getText());
        assertEquals("", driver.findElement(By.id("search-items-result")).getText());
    }

    // --- GET /api/items/{id} ---

    @Test
    void testGetItem() {
        clickAndWaitForResult("get-item-btn", "get-item-result");
        assertEquals("1:item-1", getResultText("get-item-result"));
    }

    @Test
    void testGetItemNoError() {
        clickAndWaitForResult("get-item-btn", "get-item-result");
        assertFalse(getResultText("get-item-result").startsWith("ERROR:"));
    }

    @Test
    void testGetItemResultFormat() {
        clickAndWaitForResult("get-item-btn", "get-item-result");
        String result = getResultText("get-item-result");
        assertTrue(result.contains(":"));
        String[] parts = result.split(":");
        assertEquals(2, parts.length);
    }

    @Test
    void testGetItemRepeatedCall() {
        clickAndWaitForResult("get-item-btn", "get-item-result");
        assertEquals("1:item-1", getResultText("get-item-result"));

        driver.get(baseUrl.toString());
        clickAndWaitForResult("get-item-btn", "get-item-result");
        assertEquals("1:item-1", getResultText("get-item-result"));
    }

    // --- GET /api/items ---

    @Test
    void testListItems() {
        clickAndWaitForResult("list-items-btn", "list-items-result");
        assertEquals("2", getResultText("list-items-result"));
    }

    @Test
    void testListItemsNoError() {
        clickAndWaitForResult("list-items-btn", "list-items-result");
        assertFalse(getResultText("list-items-result").startsWith("ERROR:"));
    }

    @Test
    void testListItemsReturnsPositiveNumber() {
        clickAndWaitForResult("list-items-btn", "list-items-result");
        int count = Integer.parseInt(getResultText("list-items-result"));
        assertTrue(count > 0);
    }

    @Test
    void testListItemsRepeatedCall() {
        clickAndWaitForResult("list-items-btn", "list-items-result");
        assertEquals("2", getResultText("list-items-result"));

        driver.get(baseUrl.toString());
        clickAndWaitForResult("list-items-btn", "list-items-result");
        assertEquals("2", getResultText("list-items-result"));
    }

    // --- POST /api/items ---

    @Test
    void testCreateItem() {
        clickAndWaitForResult("create-item-btn", "create-item-result");
        assertEquals("99:new-item", getResultText("create-item-result"));
    }

    @Test
    void testCreateItemNoError() {
        clickAndWaitForResult("create-item-btn", "create-item-result");
        assertFalse(getResultText("create-item-result").startsWith("ERROR:"));
    }

    @Test
    void testCreateItemRepeatedCall() {
        clickAndWaitForResult("create-item-btn", "create-item-result");
        assertEquals("99:new-item", getResultText("create-item-result"));

        driver.get(baseUrl.toString());
        clickAndWaitForResult("create-item-btn", "create-item-result");
        assertEquals("99:new-item", getResultText("create-item-result"));
    }

    // --- PUT /api/items/{id} ---

    @Test
    void testUpdateItem() {
        clickAndWaitForResult("update-item-btn", "update-item-result");
        assertEquals("1:updated-item", getResultText("update-item-result"));
    }

    @Test
    void testUpdateItemNoError() {
        clickAndWaitForResult("update-item-btn", "update-item-result");
        assertFalse(getResultText("update-item-result").startsWith("ERROR:"));
    }

    @Test
    void testUpdateItemPreservesId() {
        clickAndWaitForResult("update-item-btn", "update-item-result");
        assertTrue(getResultText("update-item-result").startsWith("1:"));
    }

    @Test
    void testUpdateItemChangesName() {
        clickAndWaitForResult("update-item-btn", "update-item-result");
        assertTrue(getResultText("update-item-result").endsWith("updated-item"));
    }

    @Test
    void testUpdateItemRepeatedCall() {
        clickAndWaitForResult("update-item-btn", "update-item-result");
        assertEquals("1:updated-item", getResultText("update-item-result"));

        driver.get(baseUrl.toString());
        clickAndWaitForResult("update-item-btn", "update-item-result");
        assertEquals("1:updated-item", getResultText("update-item-result"));
    }

    // --- DELETE /api/items/{id} ---

    @Test
    void testDeleteItem() {
        clickAndWaitForResult("delete-item-btn", "delete-item-result");
        assertEquals("1:deleted-1", getResultText("delete-item-result"));
    }

    @Test
    void testDeleteItemNoError() {
        clickAndWaitForResult("delete-item-btn", "delete-item-result");
        assertFalse(getResultText("delete-item-result").startsWith("ERROR:"));
    }

    @Test
    void testDeleteItemReturnsCorrectId() {
        clickAndWaitForResult("delete-item-btn", "delete-item-result");
        assertTrue(getResultText("delete-item-result").startsWith("1:"));
    }

    @Test
    void testDeleteItemRepeatedCall() {
        clickAndWaitForResult("delete-item-btn", "delete-item-result");
        assertEquals("1:deleted-1", getResultText("delete-item-result"));

        driver.get(baseUrl.toString());
        clickAndWaitForResult("delete-item-btn", "delete-item-result");
        assertEquals("1:deleted-1", getResultText("delete-item-result"));
    }

    // --- GET /api/items/search?name= (QueryParam) ---

    @Test
    void testSearchItems() {
        clickAndWaitForResult("search-items-btn", "search-items-result");
        assertEquals("1:item-1", getResultText("search-items-result"));
    }

    @Test
    void testSearchItemsNoError() {
        clickAndWaitForResult("search-items-btn", "search-items-result");
        assertFalse(getResultText("search-items-result").startsWith("ERROR:"));
    }

    @Test
    void testSearchItemsReturnsSingleMatch() {
        clickAndWaitForResult("search-items-btn", "search-items-result");
        String result = getResultText("search-items-result");
        assertFalse(result.contains(","));
    }

    @Test
    void testSearchItemsRepeatedCall() {
        clickAndWaitForResult("search-items-btn", "search-items-result");
        assertEquals("1:item-1", getResultText("search-items-result"));

        driver.get(baseUrl.toString());
        clickAndWaitForResult("search-items-btn", "search-items-result");
        assertEquals("1:item-1", getResultText("search-items-result"));
    }

    // --- Sequential / isolation ---

    @Test
    void testAllSixOperationsInSequence() {
        clickAndWaitForResult("get-item-btn", "get-item-result");
        assertEquals("1:item-1", getResultText("get-item-result"));

        clickAndWaitForResult("list-items-btn", "list-items-result");
        assertEquals("2", getResultText("list-items-result"));

        clickAndWaitForResult("create-item-btn", "create-item-result");
        assertEquals("99:new-item", getResultText("create-item-result"));

        clickAndWaitForResult("update-item-btn", "update-item-result");
        assertEquals("1:updated-item", getResultText("update-item-result"));

        clickAndWaitForResult("delete-item-btn", "delete-item-result");
        assertEquals("1:deleted-1", getResultText("delete-item-result"));

        clickAndWaitForResult("search-items-btn", "search-items-result");
        assertEquals("1:item-1", getResultText("search-items-result"));
    }

    @Test
    void testAllSixOperationsInReverseOrder() {
        clickAndWaitForResult("search-items-btn", "search-items-result");
        assertEquals("1:item-1", getResultText("search-items-result"));

        clickAndWaitForResult("delete-item-btn", "delete-item-result");
        assertEquals("1:deleted-1", getResultText("delete-item-result"));

        clickAndWaitForResult("update-item-btn", "update-item-result");
        assertEquals("1:updated-item", getResultText("update-item-result"));

        clickAndWaitForResult("create-item-btn", "create-item-result");
        assertEquals("99:new-item", getResultText("create-item-result"));

        clickAndWaitForResult("list-items-btn", "list-items-result");
        assertEquals("2", getResultText("list-items-result"));

        clickAndWaitForResult("get-item-btn", "get-item-result");
        assertEquals("1:item-1", getResultText("get-item-result"));
    }

    @Test
    void testGetItemDoesNotAffectOtherResults() {
        clickAndWaitForResult("get-item-btn", "get-item-result");
        assertEquals("", getResultText("list-items-result"));
        assertEquals("", getResultText("create-item-result"));
        assertEquals("", getResultText("update-item-result"));
        assertEquals("", getResultText("delete-item-result"));
        assertEquals("", getResultText("search-items-result"));
    }

    @Test
    void testUpdateItemDoesNotAffectOtherResults() {
        clickAndWaitForResult("update-item-btn", "update-item-result");
        assertEquals("", getResultText("get-item-result"));
        assertEquals("", getResultText("list-items-result"));
        assertEquals("", getResultText("create-item-result"));
        assertEquals("", getResultText("delete-item-result"));
        assertEquals("", getResultText("search-items-result"));
    }

    @Test
    void testDeleteItemDoesNotAffectOtherResults() {
        clickAndWaitForResult("delete-item-btn", "delete-item-result");
        assertEquals("", getResultText("get-item-result"));
        assertEquals("", getResultText("list-items-result"));
        assertEquals("", getResultText("create-item-result"));
        assertEquals("", getResultText("update-item-result"));
        assertEquals("", getResultText("search-items-result"));
    }

    @Test
    void testSearchItemsDoesNotAffectOtherResults() {
        clickAndWaitForResult("search-items-btn", "search-items-result");
        assertEquals("", getResultText("get-item-result"));
        assertEquals("", getResultText("list-items-result"));
        assertEquals("", getResultText("create-item-result"));
        assertEquals("", getResultText("update-item-result"));
        assertEquals("", getResultText("delete-item-result"));
    }

    // --- Nested Model ---

    @Test
    void testDetailedItemButtonPresent() {
        assertNotNull(driver.findElement(By.id("detailed-item-btn")));
        assertNotNull(driver.findElement(By.id("detailed-item-result")));
    }

    @Test
    void testDetailedItem() {
        clickAndWaitForResult("detailed-item-btn", "detailed-item-result");
        assertEquals("1:detailed-1:10:electronics", getResultText("detailed-item-result"));
    }

    @Test
    void testDetailedItemNoError() {
        clickAndWaitForResult("detailed-item-btn", "detailed-item-result");
        assertFalse(getResultText("detailed-item-result").startsWith("ERROR:"));
    }

    @Test
    void testDetailedItemContainsNestedCategory() {
        clickAndWaitForResult("detailed-item-btn", "detailed-item-result");
        String result = getResultText("detailed-item-result");
        assertTrue(result.contains("electronics"), "Result should contain nested category name");
    }

    // --- Empty List ---

    @Test
    void testSearchEmptyButtonPresent() {
        assertNotNull(driver.findElement(By.id("search-empty-btn")));
        assertNotNull(driver.findElement(By.id("search-empty-result")));
    }

    @Test
    void testSearchEmpty() {
        clickAndWaitForResult("search-empty-btn", "search-empty-result");
        assertEquals("EMPTY:0", getResultText("search-empty-result"));
    }

    @Test
    void testSearchEmptyNoError() {
        clickAndWaitForResult("search-empty-btn", "search-empty-result");
        assertFalse(getResultText("search-empty-result").startsWith("ERROR:"));
    }

    // --- Void Endpoint ---

    @Test
    void testDeleteVoidButtonPresent() {
        assertNotNull(driver.findElement(By.id("delete-void-btn")));
        assertNotNull(driver.findElement(By.id("delete-void-result")));
    }

    @Test
    void testDeleteVoidResultStartsEmpty() {
        assertEquals("", getResultText("delete-void-result"));
    }

    @Test
    void testDeleteVoid() {
        clickAndWaitForResult("delete-void-btn", "delete-void-result");
        assertEquals("VOID_OK", getResultText("delete-void-result"));
    }

    @Test
    void testDeleteVoidNoError() {
        clickAndWaitForResult("delete-void-btn", "delete-void-result");
        assertFalse(getResultText("delete-void-result").startsWith("ERROR:"));
    }

    // --- FullResponseCallback ---

    @Test
    void testFullResponseButtonPresent() {
        assertNotNull(driver.findElement(By.id("full-response-btn")));
        assertNotNull(driver.findElement(By.id("full-response-result")));
    }

    @Test
    void testFullResponseResultStartsEmpty() {
        assertEquals("", getResultText("full-response-result"));
    }

    @Test
    void testFullResponse() {
        clickAndWaitForResult("full-response-btn", "full-response-result");
        assertEquals("200:1:item-1", getResultText("full-response-result"));
    }

    @Test
    void testFullResponseNoError() {
        clickAndWaitForResult("full-response-btn", "full-response-result");
        assertFalse(getResultText("full-response-result").startsWith("ERROR:"));
    }

    @Test
    void testFullResponseContainsStatusCode() {
        clickAndWaitForResult("full-response-btn", "full-response-result");
        assertTrue(getResultText("full-response-result").startsWith("200:"));
    }

    // --- HTTP Error Handling ---

    @Test
    void testError404ButtonPresent() {
        assertNotNull(driver.findElement(By.id("error-404-btn")));
        assertNotNull(driver.findElement(By.id("error-404-result")));
    }

    @Test
    void testError500ButtonPresent() {
        assertNotNull(driver.findElement(By.id("error-500-btn")));
        assertNotNull(driver.findElement(By.id("error-500-result")));
    }

    @Test
    void testError404() {
        clickAndWaitForResult("error-404-btn", "error-404-result");
        assertEquals("404", getResultText("error-404-result"));
    }

    @Test
    void testError500() {
        clickAndWaitForResult("error-500-btn", "error-500-result");
        assertEquals("500", getResultText("error-500-result"));
    }

    @Test
    void testError404DoesNotShowOK() {
        clickAndWaitForResult("error-404-btn", "error-404-result");
        assertFalse(getResultText("error-404-result").equals("OK"),
                "Success callback should not be called for 404");
    }

    @Test
    void testError500DoesNotShowOK() {
        clickAndWaitForResult("error-500-btn", "error-500-result");
        assertFalse(getResultText("error-500-result").equals("OK"),
                "Success callback should not be called for 500");
    }

    // --- Promise API ---

    @Test
    void testPromiseButtonsPresent() {
        assertNotNull(driver.findElement(By.id("promise-get-item-btn")));
        assertNotNull(driver.findElement(By.id("promise-get-item-result")));
        assertNotNull(driver.findElement(By.id("promise-list-items-btn")));
        assertNotNull(driver.findElement(By.id("promise-list-items-result")));
    }

    @Test
    void testPromiseResultsStartEmpty() {
        assertEquals("", getResultText("promise-get-item-result"));
        assertEquals("", getResultText("promise-list-items-result"));
    }

    @Test
    void testPromiseGetItem() {
        clickAndWaitForResult("promise-get-item-btn", "promise-get-item-result");
        assertEquals("1:item-1", getResultText("promise-get-item-result"));
    }

    @Test
    void testPromiseGetItemNoError() {
        clickAndWaitForResult("promise-get-item-btn", "promise-get-item-result");
        assertFalse(getResultText("promise-get-item-result").startsWith("ERROR:"));
    }

    @Test
    void testPromiseListItems() {
        clickAndWaitForResult("promise-list-items-btn", "promise-list-items-result");
        assertEquals("2", getResultText("promise-list-items-result"));
    }

    @Test
    void testPromiseListItemsNoError() {
        clickAndWaitForResult("promise-list-items-btn", "promise-list-items-result");
        assertFalse(getResultText("promise-list-items-result").startsWith("ERROR:"));
    }

    // --- External API (JSONPlaceholder, qualifier test) ---

    @Test
    void testGetPostButtonPresent() {
        assertNotNull(driver.findElement(By.id("get-post-btn")));
        assertNotNull(driver.findElement(By.id("get-post-result")));
    }

    @Test
    void testGetPostResultStartsEmpty() {
        assertEquals("", getResultText("get-post-result"));
    }

    @Test
    void testGetPost() {
        clickAndWaitForResult("get-post-btn", "get-post-result");
        String result = getResultText("get-post-result");
        assertFalse(result.startsWith("ERROR:"), "Expected successful response but got: " + result);
        assertTrue(result.startsWith("1:"), "Expected post id=1, got: " + result);
        assertTrue(result.length() > 2, "Expected post title after id");
    }

    @Test
    void testGetPostNoError() {
        clickAndWaitForResult("get-post-btn", "get-post-result");
        assertFalse(getResultText("get-post-result").startsWith("ERROR:"));
    }

    @Test
    void testGetPostDoesNotAffectItemResults() {
        clickAndWaitForResult("get-post-btn", "get-post-result");
        assertEquals("", getResultText("get-item-result"));
        assertEquals("", getResultText("list-items-result"));
        assertEquals("", getResultText("create-item-result"));
    }

    // --- External API (custom @ExternalApi qualifier) ---

    @Test
    void testGetPostCustomButtonPresent() {
        assertNotNull(driver.findElement(By.id("get-post-custom-btn")));
        assertNotNull(driver.findElement(By.id("get-post-custom-result")));
    }

    @Test
    void testGetPostCustomResultStartsEmpty() {
        assertEquals("", getResultText("get-post-custom-result"));
    }

    @Test
    void testGetPostCustom() {
        driver.findElement(By.id("get-post-custom-btn")).click();
        new WebDriverWait(driver, Duration.ofSeconds(15))
            .until(d -> !d.findElement(By.id("get-post-custom-result")).getText().isEmpty());
        String result = getResultText("get-post-custom-result");
        assertFalse(result.startsWith("EXCEPTION:"),
                "Bean lookup failed: " + result);
        assertFalse(result.startsWith("ERROR:"),
                "REST call failed: " + result);
        assertTrue(result.startsWith("2:"), "Expected post id=2, got: " + result);
    }

    @Test
    void testGetPostCustomNoError() {
        clickAndWaitForResult("get-post-custom-btn", "get-post-custom-result");
        assertFalse(getResultText("get-post-custom-result").startsWith("ERROR:"));
    }

    @Test
    void testGetPostCustomDoesNotAffectOtherResults() {
        clickAndWaitForResult("get-post-custom-btn", "get-post-custom-result");
        assertEquals("", getResultText("get-item-result"));
        assertEquals("", getResultText("get-post-result"));
    }

    @Test
    void testBothQualifiedCallersWorkIndependently() {
        clickAndWaitForResult("get-post-btn", "get-post-result");
        clickAndWaitForResult("get-post-custom-btn", "get-post-custom-result");

        String namedResult = getResultText("get-post-result");
        String customResult = getResultText("get-post-custom-result");

        assertTrue(namedResult.startsWith("1:"), "Named caller should fetch post 1");
        assertTrue(customResult.startsWith("2:"), "Custom qualifier caller should fetch post 2");
    }

    // --- helpers ---

    private String getResultText(String resultId) {
        return driver.findElement(By.id(resultId)).getText();
    }

    private void clickAndWaitForResult(String buttonId, String resultId) {
        driver.findElement(By.id(buttonId)).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(d -> !d.findElement(By.id(resultId)).getText().isEmpty());
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
