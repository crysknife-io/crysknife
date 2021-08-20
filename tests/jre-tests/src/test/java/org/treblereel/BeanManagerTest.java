/*
 * Copyright © 2021 Treblereel
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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.inject.Default;
import javax.inject.Named;

import io.crysknife.client.ManagedInstance;
import org.junit.Test;
import org.treblereel.injection.managedinstance.ComponentIface;
import org.treblereel.injection.managedinstance.ComponentQualifierOne;
import org.treblereel.injection.managedinstance.ComponentQualifierTwo;
import org.treblereel.injection.named.NamedBean;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/25/21
 */
public class BeanManagerTest extends AbstractTest {

  @Test
  public void testQualiers() {
    ManagedInstance<ComponentIface> managedInstanceBean =
        app.getManagedInstanceBean().getManagedInstanceBean();
    List<ComponentIface> actualList =
        StreamSupport.stream(managedInstanceBean.spliterator(), false).collect(Collectors.toList());
    assertEquals(3, actualList.size());

    ComponentQualifierOne componentQualifierOne = new ComponentQualifierOne() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return ComponentQualifierOne.class;
      }
    };

    ComponentQualifierTwo componentQualifierTwo = new ComponentQualifierTwo() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return ComponentQualifierTwo.class;
      }
    };

    Default _default = new Default() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Default.class;
      }
    };

    ComponentIface componentDefault =
        super.app.beanManager.lookupBean(ComponentIface.class, _default).get();
    ComponentIface componentOne =
        super.app.beanManager.lookupBean(ComponentIface.class, componentQualifierOne).get();
    ComponentIface componentTwo =
        super.app.beanManager.lookupBean(ComponentIface.class, componentQualifierTwo).get();

    assertEquals("ComponentDefault", componentDefault.getComponentName());
    assertEquals("ComponentOne", componentOne.getComponentName());
    assertEquals("ComponentTwo", componentTwo.getComponentName());

    assertEquals(3, super.app.beanManager.lookupBeans(ComponentIface.class).size());

    assertEquals(1, super.app.beanManager.lookupBeans(ComponentIface.class, _default).size());
    ComponentIface _defInstance =
        super.app.beanManager.lookupBeans(ComponentIface.class, _default).iterator().next().get();
    assertEquals("ComponentDefault", _defInstance.getComponentName());

    assertEquals(1,
        super.app.beanManager.lookupBeans(ComponentIface.class, componentQualifierOne).size());
    ComponentIface _componentQualifierOne = super.app.beanManager
        .lookupBeans(ComponentIface.class, componentQualifierOne).iterator().next().get();
    assertEquals("ComponentOne", _componentQualifierOne.getComponentName());

    assertEquals(1,
        super.app.beanManager.lookupBeans(ComponentIface.class, componentQualifierTwo).size());
    ComponentIface _componentQualifierTwo = super.app.beanManager
        .lookupBeans(ComponentIface.class, componentQualifierTwo).iterator().next().get();
    assertEquals("ComponentTwo", _componentQualifierTwo.getComponentName());

    assertEquals(0, super.app.beanManager
        .lookupBeans(ComponentIface.class, componentQualifierOne, componentQualifierTwo).size());


  }

  @Test
  public void testNamed() {


    Default _default = new Default() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Default.class;
      }
    };

    Named named1 = new Named() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Named.class;
      }

      @Override
      public String value() {
        return "NamedBeanOne";
      }
    };

    Named named2 = new Named() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Named.class;
      }

      @Override
      public String value() {
        return "NamedBeanTwo";
      }
    };

    NamedBean componentDefault = super.app.beanManager.lookupBean(NamedBean.class, _default).get();
    NamedBean componentOne = super.app.beanManager.lookupBean(NamedBean.class, named1).get();
    NamedBean componentTwo = super.app.beanManager.lookupBean(NamedBean.class, named2).get();

    assertEquals("org.treblereel.injection.named.NamedBeanDefault", componentDefault.say());
    assertEquals("org.treblereel.injection.named.NamedBeanOne", componentOne.say());
    assertEquals("org.treblereel.injection.named.NamedBeanTwo", componentTwo.say());
    assertEquals(5, super.app.beanManager.lookupBeans(NamedBean.class).size());


    assertEquals(5, super.app.beanManager.lookupBeans(NamedBean.class).size());

    assertEquals(1, super.app.beanManager.lookupBeans(NamedBean.class, _default).size());
    NamedBean _defInstance =
        super.app.beanManager.lookupBeans(NamedBean.class, _default).iterator().next().get();
    assertEquals("org.treblereel.injection.named.NamedBeanDefault", _defInstance.say());

    assertEquals(1, super.app.beanManager.lookupBeans(NamedBean.class, named1).size());
    NamedBean _componentQualifierOne =
        super.app.beanManager.lookupBeans(NamedBean.class, named1).iterator().next().get();
    assertEquals("org.treblereel.injection.named.NamedBeanOne", _componentQualifierOne.say());

    assertEquals(1, super.app.beanManager.lookupBeans(NamedBean.class, named2).size());


    NamedBean _componentQualifierTwo =
        super.app.beanManager.lookupBeans(NamedBean.class, named2).iterator().next().get();
    assertEquals("org.treblereel.injection.named.NamedBeanTwo", _componentQualifierTwo.say());

    assertEquals(0, super.app.beanManager.lookupBeans(NamedBean.class, named1, named2).size());


  }
}
