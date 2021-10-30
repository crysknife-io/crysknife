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
import org.treblereel.injection.inheritance.Target;
import org.treblereel.postconstruct.ChildFour;
import org.treblereel.postconstruct.ChildThree;
import org.treblereel.postconstruct.ChildTwo;
import org.treblereel.postconstruct.PostConstructChild;
import org.treblereel.postconstruct.PostConstructParent;

import javax.inject.Named;

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class InheritanceTest extends AbstractTest {

  @Test
  public void testPostConstructAppBootstrap() {
    assertEquals("PostConstructChild", app.testPostConstruct);
  }

  @Test
  public void testSimpleBeanSingleton() {
    assertEquals("done", app.getSimpleBeanSingleton().getPostConstruct());
  }

  @Test
  public void testSimpleBeanApplicationScoped() {
    assertEquals("done", app.getSimpleBeanApplicationScoped().getPostConstruct());
  }

  @Test
  public void testSimpleDependent() {
    assertEquals("done", app.getSimpleBeanDependent().getPostConstruct());
  }

  @Test
  public void testChildPostConstructCalled() {
    assertEquals(1, app.postConstructs.child.calls.size());
    assertEquals("Parent", app.postConstructs.child.calls.get(0));

    app.beanManager.lookupBeans(ChildTwo.class).forEach(bean -> {
      if (bean.getName().equals(ChildTwo.class.getCanonicalName())) {
        assertEquals(ChildTwo.class, bean.getInstance().getClass());
        assertEquals(2, bean.getInstance().calls.size());
        assertEquals("Parent", bean.getInstance().calls.get(0));
        assertEquals("ChildTwo", bean.getInstance().calls.get(1));
      } else if (bean.getName().equals(ChildThree.class.getCanonicalName())) {
        assertEquals(2, bean.getInstance().calls.size());
      } else if (bean.getName().equals(ChildFour.class.getCanonicalName())) {
        assertEquals(3, bean.getInstance().calls.size());
        assertEquals("ChildFour", bean.getInstance().calls.get(2));
      }
    });
  }

  @Test
  public void testPrivatePostConstructCalled() {
    PostConstructParent postConstructParent =
        app.beanManager.lookupBean(PostConstructParent.class, new Named() {
          @Override
          public Class<? extends Annotation> annotationType() {
            return Named.class;
          }

          @Override
          public String value() {
            return "PostConstructParent";
          }
        }).getInstance();
    PostConstructChild postConstructChild =
        app.beanManager.lookupBean(PostConstructChild.class).getInstance();


    assertEquals(PostConstructParent.class.getSimpleName(),
        postConstructParent.postConstructParent);
    assertEquals(PostConstructChild.class.getSimpleName(), postConstructChild.postConstructChild);
    assertEquals(PostConstructParent.class.getSimpleName(), postConstructChild.postConstructParent);

    app.beanManager.lookupBeans(ChildTwo.class).forEach(bean -> {
      if (bean.getName().equals(ChildTwo.class.getCanonicalName())) {
        assertEquals(ChildTwo.class, bean.getInstance().getClass());
        assertEquals(2, bean.getInstance().calls.size());
        assertEquals("Parent", bean.getInstance().calls.get(0));
        assertEquals("ChildTwo", bean.getInstance().calls.get(1));
      } else if (bean.getName().equals(ChildThree.class.getCanonicalName())) {
        assertEquals(2, bean.getInstance().calls.size());
      } else if (bean.getName().equals(ChildFour.class.getCanonicalName())) {
        assertEquals(3, bean.getInstance().calls.size());
        assertEquals("ChildFour", bean.getInstance().calls.get(2));
      }
    });
  }

  @Test
  public void testParentFieldInjected() {

    assertNotNull(app.inheritanceBean);
    assertNotNull(app.inheritanceBean.getBean());
    assertNotNull(app.inheritanceBean.getBean().getTarget());
    assertNotNull(app.inheritanceBean.getBean().getParentTarget());
    assertNotNull(app.inheritanceBean.getBean().getParentTarget());
    assertNotNull(app.inheritanceBean.getBean().getParentTarget().hello());

    assertEquals(Target.class, app.inheritanceBean.getBean().getTarget().getClass());
    assertEquals(Target.class, app.inheritanceBean.getBean().getParentTarget().getClass());
    assertEquals(Target.class.getCanonicalName(),
        app.inheritanceBean.getBean().getTarget().hello());
    assertEquals(Target.class.getCanonicalName(),
        app.inheritanceBean.getBean().getParentTarget().hello());
  }

}
