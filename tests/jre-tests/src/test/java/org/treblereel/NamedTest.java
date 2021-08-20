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
import org.treblereel.injection.named.NamedBean;
import org.treblereel.injection.named.NamedBeanDefault;
import org.treblereel.injection.named.NamedBeanOne;
import org.treblereel.injection.named.NamedBeanTwo;
import org.treblereel.injection.qualifiers.QualifierBean;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class NamedTest extends AbstractTest {

  @Test
  public void testNamedConstructorInjection() {
    assertEquals(NamedBeanDefault.class.getSimpleName(),
        app.getNamedTestBean().namedConstructorInjection.def.getClass().getSimpleName());
    assertEquals(NamedBeanOne.class.getSimpleName(),
        app.getNamedTestBean().namedConstructorInjection.one.getClass().getSimpleName());
    assertEquals(NamedBeanTwo.class.getSimpleName(),
        app.getNamedTestBean().namedConstructorInjection.two.getClass().getSimpleName());
  }

  @Test
  public void testNamedFieldInjection() {
    assertEquals(NamedBeanDefault.class.getSimpleName(),
        app.getNamedTestBean().namedFieldInjection.def.getClass().getSimpleName());
    assertEquals(NamedBeanOne.class.getSimpleName(),
        app.getNamedTestBean().namedFieldInjection.one.getClass().getSimpleName());
    assertEquals(NamedBeanTwo.class.getSimpleName(),
        app.getNamedTestBean().namedFieldInjection.two.getClass().getSimpleName());
  }

  @Test
  public void testBeanManager() {
    assertEquals(5, app.beanManager.lookupBeans(NamedBean.class).size());
    List<String> beans = new ArrayList<>();
    for (Instance lookupBean : app.beanManager.lookupBeans(NamedBean.class)) {
      beans.add(((NamedBean) lookupBean.get()).say());
    }
    assertTrue(beans.contains(NamedBeanDefault.class.getCanonicalName()));
    assertTrue(beans.contains(NamedBeanOne.class.getCanonicalName()));
    assertTrue(beans.contains(NamedBeanTwo.class.getCanonicalName()));

    Named namedBeanOne = new Named() {

      public Class<? extends Annotation> annotationType() {
        return javax.inject.Named.class;
      }

      public String value() {
        return "NamedBeanOne";
      }
    };

    Named namedBeanTwo = new Named() {

      public Class<? extends Annotation> annotationType() {
        return javax.inject.Named.class;
      }

      public String value() {
        return "NamedBeanTwo";
      }
    };

    assertEquals(NamedBeanDefault.class,
        app.beanManager.lookupBean(NamedBean.class).get().getClass());
    assertEquals(NamedBeanOne.class,
        app.beanManager.lookupBean(NamedBean.class, namedBeanOne).get().getClass());
    assertEquals(NamedBeanTwo.class,
        app.beanManager.lookupBean(NamedBean.class, namedBeanTwo).get().getClass());

  }
}
