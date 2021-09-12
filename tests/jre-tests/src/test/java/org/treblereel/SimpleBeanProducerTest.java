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
import org.treblereel.produces.scoped.URLPatternMatcherHolder;
import org.treblereel.produces.staticproduces.MyStaticBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    assertNotNull(app.getSimpleBeanProducerTest().getSimpleBeanDependentOne());
    assertNotNull(app.getSimpleBeanProducerTest().getSimpleBeanDependentTwo());
    assertNotNull(app.getSimpleBeanProducerTest().getSimpleBeanSingletonOne());
    assertNotNull(app.getSimpleBeanProducerTest().getSimpleBeanSingletonTwo());
  }

  @Test
  public void testQualifierBeanProducerTest() {
    assertNotNull(app.getQualifierBeanProducerTest().getQualifierBean());
    assertEquals("REDHAT", app.getQualifierBeanProducerTest().getQualifierBean().say());
  }

  @Test
  public void testURLPatternMatcherTest() {
    assertNotNull(app.beanManager.<URLPatternMatcherHolder>lookupBean(URLPatternMatcherHolder.class)
        .get().matcher);
    assertNotNull(app.beanManager.<URLPatternMatcherHolder>lookupBean(URLPatternMatcherHolder.class)
        .get().matcher.test());
    assertEquals("URLPatternMatcherProvider", app.beanManager
        .<URLPatternMatcherHolder>lookupBean(URLPatternMatcherHolder.class).get().matcher.test());
  }

  @Test
  public void testAppStaticBean() {
    assertNotNull(app.getSimpleBeanProducerTest().getMyStaticBean());
    assertTrue(app.getSimpleBeanProducerTest().getMyStaticBean().ready);
    assertEquals(MyStaticBean.class.getCanonicalName(),
        app.getSimpleBeanProducerTest().getMyStaticBean().whoami());

    assertNotNull(app.beanManager.<MyStaticBean>lookupBean(MyStaticBean.class).get());
    assertTrue(app.beanManager.<MyStaticBean>lookupBean(MyStaticBean.class).get().ready);

    assertEquals(MyStaticBean.class.getCanonicalName(),
        app.beanManager.<MyStaticBean>lookupBean(MyStaticBean.class).get().whoami());


  }


}
