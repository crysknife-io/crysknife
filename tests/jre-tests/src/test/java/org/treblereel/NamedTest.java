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

import io.crysknife.client.SyncBeanDef;
import org.junit.Test;
import org.treblereel.injection.named.NamedBean;
import org.treblereel.injection.named.NamedBeanDefault;
import org.treblereel.injection.named.NamedBeanOne;
import org.treblereel.injection.named.NamedBeanTwo;
import org.treblereel.injection.qualifiers.QualifierBean;

import javax.enterprise.inject.Instance;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    Set<NamedBean> beans = new HashSet<>();
    for (SyncBeanDef<NamedBean> lookupBean : app.beanManager
        .<NamedBean>lookupBeans(NamedBean.class)) {
      beans.add(lookupBean.getInstance());
    }


    assertEquals(6, beans.size());
    List<String> result = new ArrayList<>();
    for (SyncBeanDef lookupBean : app.beanManager.lookupBeans(NamedBean.class)) {
      result.add(((NamedBean) lookupBean.getInstance()).say());
    }
    assertTrue(result.contains(NamedBeanDefault.class.getCanonicalName()));
    assertTrue(result.contains(NamedBeanOne.class.getCanonicalName()));
    assertTrue(result.contains(NamedBeanTwo.class.getCanonicalName()));

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
        app.beanManager.lookupBean(NamedBean.class).getInstance().getClass());
    assertEquals(NamedBeanOne.class,
        app.beanManager.lookupBean(NamedBean.class, namedBeanOne).getInstance().getClass());
    assertEquals(NamedBeanTwo.class,
        app.beanManager.lookupBean(NamedBean.class, namedBeanTwo).getInstance().getClass());

  }
}
