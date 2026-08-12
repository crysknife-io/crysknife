/*
 * Copyright © 2024 Treblereel
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
package org.treblereel.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.treblereel.AbstractTest;

public class InterceptorTest extends AbstractTest {

  @Test
  public void testInterceptedServiceIsInjected() {
    assertNotNull(app.interceptedService);
    assertNotNull(app.loggingInterceptor);
  }

  @Test
  public void testVoidMethodIntercepted() {
    app.loggingInterceptor.clearInvocations();
    app.interceptedService.reset();

    app.interceptedService.doWork();

    assertTrue(app.interceptedService.isDoWorkCalled());
    assertEquals(2, app.loggingInterceptor.getInvocations().size());
    assertEquals("before:doWork", app.loggingInterceptor.getInvocations().get(0));
    assertEquals("after:doWork", app.loggingInterceptor.getInvocations().get(1));
  }

  @Test
  public void testMethodWithReturnValue() {
    app.loggingInterceptor.clearInvocations();
    app.interceptedService.reset();

    String result = app.interceptedService.greet("World");

    assertEquals("Hello, World", result);
    assertEquals("World", app.interceptedService.getLastArg());
    assertEquals(2, app.loggingInterceptor.getInvocations().size());
    assertEquals("before:greet", app.loggingInterceptor.getInvocations().get(0));
    assertEquals("after:greet", app.loggingInterceptor.getInvocations().get(1));
  }

  @Test
  public void testNonAnnotatedMethodNotIntercepted() {
    app.loggingInterceptor.clearInvocations();
    app.interceptedService.reset();

    app.interceptedService.notIntercepted();

    assertTrue(app.interceptedService.isDoWorkCalled());
    assertTrue(app.loggingInterceptor.getInvocations().isEmpty());
  }
}
