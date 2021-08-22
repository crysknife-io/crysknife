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

import org.junit.Test;
import org.treblereel.injection.qualifiers.QualifierBean;
import org.treblereel.injection.qualifiers.QualifierBeanDefault;
import org.treblereel.injection.qualifiers.QualifierBeanOne;
import org.treblereel.injection.qualifiers.QualifierBeanTwo;
import org.treblereel.injection.qualifiers.QualifierConstructorInjection;
import org.treblereel.injection.qualifiers.QualifierOne;
import org.treblereel.injection.qualifiers.QualifierTwo;

import javax.enterprise.inject.Instance;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    Set<QualifierBean> beans = new HashSet<>();
    for (Instance<QualifierBean> lookupBean : app.beanManager
        .<QualifierBean>lookupBeans(QualifierBean.class)) {
      beans.add(lookupBean.get());
    }

    assertEquals(3, beans.size());
    List<String> result = new ArrayList<>();
    for (Instance lookupBean : app.beanManager.lookupBeans(QualifierBean.class)) {
      result.add(((QualifierBean) lookupBean.get()).say());
    }
    assertTrue(result.contains("org.treblereel.injection.qualifiers.QualifierBeanDefault"));
    assertTrue(result.contains("org.treblereel.injection.qualifiers.QualifierBeanOne"));
    assertTrue(result.contains("org.treblereel.injection.qualifiers.QualifierBeanTwo"));

    QualifierOne qualifierOne = new org.treblereel.injection.qualifiers.QualifierOne() {

      public Class<? extends Annotation> annotationType() {
        return org.treblereel.injection.qualifiers.QualifierOne.class;
      }
    };

    QualifierTwo qualifierTwo = new org.treblereel.injection.qualifiers.QualifierTwo() {

      public Class<? extends Annotation> annotationType() {
        return org.treblereel.injection.qualifiers.QualifierTwo.class;
      }
    };

    assertEquals(QualifierBeanOne.class,
        app.beanManager.lookupBean(QualifierBean.class, qualifierOne).get().getClass());
    assertEquals(QualifierBeanTwo.class,
        app.beanManager.lookupBean(QualifierBean.class, qualifierTwo).get().getClass());
    assertEquals(QualifierBeanDefault.class,
        app.beanManager.lookupBean(QualifierBean.class).get().getClass());


  }
}
