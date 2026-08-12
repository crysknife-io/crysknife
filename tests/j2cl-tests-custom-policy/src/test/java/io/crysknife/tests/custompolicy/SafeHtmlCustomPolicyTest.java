/*
 * Copyright © 2023 Treblereel
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

package io.crysknife.tests.custompolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.nio.file.Path;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SafeHtmlCustomPolicyTest {

  private static ChromeDriver driver;

  @BeforeClass
  public static void setupClass() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless", "--window-size=1920,1200");
    driver = new ChromeDriver(options);
  }

  @Before
  public void setup() throws MalformedURLException {
    Path path = Path.of("target", "j2cl", "launcherDir", "index.html");
    driver.get(path.toUri().toURL().toString());
    assertEquals("TrustedTypes Custom Policy Test", driver.getTitle());
  }

  @Test
  public void testCustomPolicySetInnerHTML() {
    WebElement result = driver.findElement(By.id("trusted-types-result"));
    assertNotNull(result);
    assertEquals("<span>policy-ok</span>", result.getAttribute("innerHTML"));
  }

  @Test
  public void testCustomPolicyNoCSPViolation() {
    // If the policy name didn't match the CSP header, createPolicy() would throw
    // and setInnerHTML would fail — the element wouldn't exist in the DOM
    WebElement result = driver.findElement(By.id("trusted-types-result"));
    assertEquals("policy-ok", result.getText());
  }

  @AfterClass
  public static void afterClass() {
    if (driver != null) {
      driver.quit();
    }
  }
}
