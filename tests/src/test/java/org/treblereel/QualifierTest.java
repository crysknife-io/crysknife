package org.treblereel;

import org.junit.Test;
import org.treblereel.injection.qualifiers.QualifierBeanDefault;
import org.treblereel.injection.qualifiers.QualifierBeanOne;
import org.treblereel.injection.qualifiers.QualifierBeanTwo;
import org.treblereel.injection.qualifiers.QualifierConstructorInjection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class QualifierTest extends AbstractTest {

  @Test
  public void testQualifierFieldInjectionBean() {
    assertEquals(QualifierBeanDefault.class.getSimpleName(),
        app.getQualifierFieldInjection().getQualifierBeanDefault().getClass().getSimpleName());
    assertEquals(QualifierBeanOne.class.getSimpleName(),
        app.getQualifierFieldInjection().qualifierBeanOne.getClass().getSimpleName());
    assertEquals(QualifierBeanTwo.class.getSimpleName(),
        app.getQualifierFieldInjection().qualifierBeanTwo.getClass().getSimpleName());
  }

  @Test
  public void testAppSimpleBean() {
    assertNotNull(app.getQualifierConstructorInjection());
    assertEquals(QualifierConstructorInjection.class.getSimpleName(),
        app.getQualifierConstructorInjection().getClass().getSimpleName());
    assertEquals(QualifierBeanOne.class,
        app.getQualifierConstructorInjection().qualifierBeanOne.getClass());
    assertEquals(QualifierBeanTwo.class,
        app.getQualifierConstructorInjection().qualifierBeanTwo.getClass());
  }
}
