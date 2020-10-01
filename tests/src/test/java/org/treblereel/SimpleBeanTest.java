package org.treblereel;

import org.junit.Test;
import org.treblereel.injection.applicationscoped.SimpleBeanApplicationScoped;
import org.treblereel.injection.qualifiers.QualifierBeanOne;
import org.treblereel.injection.qualifiers.QualifierBeanTwo;
import org.treblereel.injection.qualifiers.QualifierConstructorInjection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/10/19
 */
public class SimpleBeanTest extends AbstractTest {

  @Test
  public void testAppSimpleBean() {
    assertNotNull(app.getSimpleBeanApplicationScoped());
    assertEquals(SimpleBeanApplicationScoped.class.getSimpleName(),
        app.getSimpleBeanApplicationScoped().getName());

    assertNotNull(app.getQualifierConstructorInjection());
    assertEquals(QualifierConstructorInjection.class.getSimpleName(),
        app.getQualifierConstructorInjection().getClass().getSimpleName());
    assertEquals(QualifierBeanOne.class,
        app.getQualifierConstructorInjection().qualifierBeanOne.getClass());
    assertEquals(QualifierBeanTwo.class,
        app.getQualifierConstructorInjection().qualifierBeanTwo.getClass());
  }
}
