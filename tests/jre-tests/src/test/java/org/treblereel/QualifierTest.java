/*
 * Copyright © 2020 Treblereel
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

package org.treblereel;

import io.crysknife.client.Instance;
import org.junit.Test;
import org.treblereel.injection.qualifiers.QualifierBean;
import org.treblereel.injection.qualifiers.QualifierBeanDefault;
import org.treblereel.injection.qualifiers.QualifierBeanOne;
import org.treblereel.injection.qualifiers.QualifierBeanTwo;
import org.treblereel.injection.qualifiers.QualifierConstructorInjection;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

  @Test
  public void testBeanManager() {
    assertEquals(3, app.beanManager.lookupBeans(QualifierBean.class).size());
    List<String> beans = new ArrayList<>();
    for (Instance lookupBean : app.beanManager.lookupBeans(QualifierBean.class)) {
      beans.add(((QualifierBean) lookupBean.get()).say());
    }
    assertTrue(beans.contains("org.treblereel.injection.qualifiers.QualifierBeanDefault"));
    assertTrue(beans.contains("org.treblereel.injection.qualifiers.QualifierBeanOne"));
    assertTrue(beans.contains("org.treblereel.injection.qualifiers.QualifierBeanTwo"));
  }
}
