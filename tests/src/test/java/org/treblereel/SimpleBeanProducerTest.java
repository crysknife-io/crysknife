package org.treblereel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class SimpleBeanProducerTest extends AbstractTest {

  @Test
  public void testAppSimpleBean() {
    assertNotEquals(app.getSimpleBeanProducerTest().getSimpleBeanDependentOne(),
        app.getSimpleBeanProducerTest().getSimpleBeanDependentTwo());
    assertEquals(app.getSimpleBeanProducerTest().getSimpleBeanSingletonOne(),
        app.getSimpleBeanProducerTest().getSimpleBeanSingletonTwo());
  }

  // @Test
  public void testQualifierBeanProducerTest() {
    assertEquals("ZZZ", app.getQualifierBeanProducerTest().getQualifierBean().say());
  }
}
