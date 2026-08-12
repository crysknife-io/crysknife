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
package org.treblereel.decorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.decorator.chain.BasicFormatter;
import org.treblereel.decorator.chain.BracketFormatterDecorator;
import org.treblereel.decorator.chain.StarFormatterDecorator;
import org.treblereel.decorator.exception.StrictValidator;
import org.treblereel.decorator.exception.TrimValidatorDecorator;
import org.treblereel.decorator.lifecycle.PostConstructWorkerDecorator;
import org.treblereel.decorator.lifecycle.SimpleWorker;
import org.treblereel.decorator.multiface.LoggingPrinterDecorator;
import org.treblereel.decorator.multiface.MultiDevice;

public class DecoratorTest extends AbstractTest {

  // === Basic decorator tests ===

  @Test
  public void testGreeterIsInjected() {
    assertNotNull(app.greeter);
    assertNotNull(app.loggingGreeterDecorator);
    assertNotNull(app.simpleGreeter);
  }

  @Test
  public void testGreeterReturnsDecoratorInstance() {
    assertTrue("@Inject Greeter should return the decorator, not the original",
        app.greeter instanceof LoggingGreeterDecorator);
  }

  @Test
  public void testDecoratorDelegatesToOriginal() {
    String result = app.greeter.greet("World");
    assertEquals("Hello, World", result);
  }

  @Test
  public void testDecoratorLogsInvocation() {
    app.loggingGreeterDecorator.clearLog();
    app.greeter.greet("Test");
    assertEquals(1, app.loggingGreeterDecorator.getLog().size());
    assertEquals("greet:Test", app.loggingGreeterDecorator.getLog().get(0));
  }

  @Test
  public void testConcreteTypeReturnsOriginal() {
    assertTrue("@Inject SimpleGreeter should return the original bean",
        app.simpleGreeter instanceof SimpleGreeter);
    assertEquals("Hello, CDI", app.simpleGreeter.greet("CDI"));
  }

  // === Decorator chain tests ===

  @Test
  public void testChainAllInjected() {
    assertNotNull(app.formatter);
    assertNotNull(app.basicFormatter);
    assertNotNull(app.bracketFormatterDecorator);
    assertNotNull(app.starFormatterDecorator);
  }

  @Test
  public void testChainOutermostDecoratorIsInjected() {
    assertTrue("@Inject Formatter should return the outermost decorator (StarFormatterDecorator)",
        app.formatter instanceof StarFormatterDecorator);
  }

  @Test
  public void testChainExecutionOrder() {
    // Star(@Priority 2000) is outermost, Bracket(@Priority 1000) is innermost
    // Chain: Star -> Bracket -> Basic
    // Star wraps with *, Bracket wraps with []
    String result = app.formatter.format("hello");
    assertEquals("*[hello]*", result);
  }

  @Test
  public void testChainConcreteTypeBypassesDecorators() {
    String result = app.basicFormatter.format("hello");
    assertEquals("hello", result);
  }

  @Test
  public void testChainInnerDecoratorByConcreteType() {
    assertTrue(app.bracketFormatterDecorator instanceof BracketFormatterDecorator);
  }

  @Test
  public void testChainWithEmptyString() {
    String result = app.formatter.format("");
    assertEquals("*[]*", result);
  }

  // === Multi-interface tests ===

  @Test
  public void testMultiInterfaceAllInjected() {
    assertNotNull(app.printer);
    assertNotNull(app.scanner);
    assertNotNull(app.multiDevice);
    assertNotNull(app.loggingPrinterDecorator);
  }

  @Test
  public void testMultiInterfaceDecoratedInterfaceReturnsDecorator() {
    assertTrue("@Inject Printer should return LoggingPrinterDecorator",
        app.printer instanceof LoggingPrinterDecorator);
  }

  @Test
  public void testMultiInterfaceNonDecoratedInterfaceReturnsOriginal() {
    assertTrue("@Inject Scanner should return the original MultiDevice",
        app.scanner instanceof MultiDevice);
  }

  @Test
  public void testMultiInterfaceDecoratedDelegation() {
    String result = app.printer.print("test");
    assertEquals("LOG:printed:test", result);
  }

  @Test
  public void testMultiInterfaceNonDecoratedBehavior() {
    String result = app.scanner.scan();
    assertEquals("scanned", result);
  }

  @Test
  public void testMultiInterfaceConcreteTypeReturnsOriginal() {
    assertTrue(app.multiDevice instanceof MultiDevice);
    assertEquals("printed:direct", app.multiDevice.print("direct"));
    assertEquals("scanned", app.multiDevice.scan());
  }

  // === Lifecycle tests ===

  @Test
  public void testPostConstructCalledOnDecorator() {
    assertTrue("@PostConstruct should be called on decorator",
        app.postConstructWorkerDecorator.isInitialized());
  }

  @Test
  public void testPostConstructDecoratorDelegation() {
    String result = app.worker.work();
    assertEquals("PC:done", result);
  }

  @Test
  public void testWorkerInterfaceReturnsDecorator() {
    assertTrue(app.worker instanceof PostConstructWorkerDecorator);
  }

  @Test
  public void testSimpleWorkerConcreteType() {
    assertTrue(app.simpleWorker instanceof SimpleWorker);
    assertEquals("done", app.simpleWorker.work());
  }

  // === Exception propagation tests ===

  @Test
  public void testExceptionPropagatesThroughDecorator() {
    assertTrue(app.validator instanceof TrimValidatorDecorator);

    try {
      app.validator.validate(null);
      fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("Input must not be null", e.getMessage());
    }
  }

  @Test
  public void testDecoratorTrimsBeforeDelegation() {
    String result = app.validator.validate("  hello  ");
    assertEquals("valid:hello", result);
  }

  @Test
  public void testDecoratorTrimsEmptyToEmpty() {
    try {
      app.validator.validate("   ");
      fail("Should have thrown IllegalStateException for empty after trim");
    } catch (IllegalStateException e) {
      assertEquals("Input must not be empty", e.getMessage());
    }
  }

  @Test
  public void testValidatorConcreteTypeBypassesDecorator() {
    assertTrue(app.strictValidator instanceof StrictValidator);
    assertEquals("valid:  spaces  ", app.strictValidator.validate("  spaces  "));
  }

  // === Singleton / identity tests ===

  @Test
  public void testDecoratorAndInterfaceReturnSameInstance() {
    assertSame("@Inject Greeter and @Inject LoggingGreeterDecorator should be the same instance",
        app.greeter, app.loggingGreeterDecorator);
  }

  @Test
  public void testConcreteOriginalIsDifferentFromDecorator() {
    assertNotSame("Decorator and original bean should be different instances",
        app.greeter, app.simpleGreeter);
  }

  // === Multiple calls / state tests ===

  @Test
  public void testDecoratorAccumulatesState() {
    app.loggingGreeterDecorator.clearLog();
    app.greeter.greet("A");
    app.greeter.greet("B");
    app.greeter.greet("C");
    assertEquals(3, app.loggingGreeterDecorator.getLog().size());
    assertEquals("greet:A", app.loggingGreeterDecorator.getLog().get(0));
    assertEquals("greet:B", app.loggingGreeterDecorator.getLog().get(1));
    assertEquals("greet:C", app.loggingGreeterDecorator.getLog().get(2));
  }
}
