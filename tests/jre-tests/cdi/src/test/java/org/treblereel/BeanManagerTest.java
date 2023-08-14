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

import io.crysknife.client.SyncBeanDef;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Named;

import io.crysknife.client.ManagedInstance;
import org.junit.Test;
import org.treblereel.injection.managedinstance.ComponentIface;
import org.treblereel.injection.managedinstance.ComponentQualifierOne;
import org.treblereel.injection.managedinstance.ComponentQualifierTwo;
import org.treblereel.injection.named.NamedBean;
import org.treblereel.injection.named.NamedBeanSubThree;
import org.treblereel.injection.qualifiers.QualifierBean;
import org.treblereel.injection.qualifiers.QualifierBeanTwo;

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

    ComponentIface componentDefault = super.app.beanManager
        .<ComponentIface>lookupBean(ComponentIface.class, _default).getInstance();
    ComponentIface componentOne = super.app.beanManager
        .<ComponentIface>lookupBean(ComponentIface.class, componentQualifierOne).getInstance();
    ComponentIface componentTwo = super.app.beanManager
        .<ComponentIface>lookupBean(ComponentIface.class, componentQualifierTwo).getInstance();

    assertEquals("ComponentDefault", componentDefault.getComponentName());
    assertEquals("ComponentOne", componentOne.getComponentName());
    assertEquals("ComponentTwo", componentTwo.getComponentName());

    assertEquals(3,
        StreamSupport
            .stream(super.app.beanManager.lookupBeans(ComponentIface.class).spliterator(), false)
            .count());

    assertEquals(3, StreamSupport.stream(
        super.app.beanManager.lookupBeans(ComponentIface.class.getCanonicalName()).spliterator(),
        false).count());

    assertEquals(1,
        StreamSupport
            .stream(super.app.beanManager.lookupBeans(ComponentIface.class, _default).spliterator(),
                false)
            .count());
    ComponentIface _defInstance =
        super.app.beanManager.<ComponentIface>lookupBeans(ComponentIface.class, _default).iterator()
            .next().getInstance();
    assertEquals("ComponentDefault", _defInstance.getComponentName());

    assertEquals(1,
        StreamSupport.stream(super.app.beanManager
            .lookupBeans(ComponentIface.class, componentQualifierOne).spliterator(), false)
            .count());
    ComponentIface _componentQualifierOne = super.app.beanManager
        .<ComponentIface>lookupBeans(ComponentIface.class, componentQualifierOne).iterator().next()
        .getInstance();
    assertEquals("ComponentOne", _componentQualifierOne.getComponentName());

    assertEquals(1,
        StreamSupport.stream(super.app.beanManager
            .lookupBeans(ComponentIface.class, componentQualifierTwo).spliterator(), false)
            .count());
    ComponentIface _componentQualifierTwo = super.app.beanManager
        .<ComponentIface>lookupBeans(ComponentIface.class, componentQualifierTwo).iterator().next()
        .getInstance();
    assertEquals("ComponentTwo", _componentQualifierTwo.getComponentName());

    assertEquals(0,
        StreamSupport.stream(super.app.beanManager
            .lookupBeans(ComponentIface.class, componentQualifierOne, componentQualifierTwo)
            .spliterator(), false).count());
  }

  @Test
  public void testLookupBeansByName() {
    assertEquals(NamedBeanSubThree.class,
        app.beanManager.lookupBeans("NamedBeanSubThree").iterator().next().getType());
    assertEquals(QualifierBeanTwo.class, app.beanManager
        .lookupBeans(QualifierBeanTwo.class.getCanonicalName()).iterator().next().getType());
    assertEquals(QualifierBeanTwo.class.getCanonicalName(), app.beanManager
        .lookupBeans(QualifierBeanTwo.class.getCanonicalName()).iterator().next().getName());
    assertEquals(3, app.beanManager.lookupBeans(QualifierBean.class.getCanonicalName()).size());
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

    NamedBean componentDefault =
        super.app.beanManager.<NamedBean>lookupBean(NamedBean.class, _default).getInstance();
    NamedBean componentOne =
        super.app.beanManager.<NamedBean>lookupBean(NamedBean.class, named1).getInstance();
    NamedBean componentTwo =
        super.app.beanManager.<NamedBean>lookupBean(NamedBean.class, named2).getInstance();

    assertEquals("org.treblereel.injection.named.NamedBeanDefault", componentDefault.say());
    assertEquals("org.treblereel.injection.named.NamedBeanOne", componentOne.say());
    assertEquals("org.treblereel.injection.named.NamedBeanTwo", componentTwo.say());
    assertEquals(6, StreamSupport
        .stream(super.app.beanManager.lookupBeans(NamedBean.class).spliterator(), false).count());
    assertEquals(6,
        StreamSupport.stream(
            super.app.beanManager.lookupBeans(NamedBean.class.getCanonicalName()).spliterator(),
            false).count());

    assertEquals(1, StreamSupport
        .stream(super.app.beanManager.lookupBeans(NamedBean.class, _default).spliterator(), false)
        .count());


    assertEquals(1,
        StreamSupport
            .stream(super.app.beanManager.lookupBeans("NamedBeanSubThree").spliterator(), false)
            .count());
    assertEquals("NamedBeanSubThree",
        app.beanManager.lookupBeans("NamedBeanSubThree").iterator().next().getName());
    assertEquals(NamedBeanSubThree.class,
        app.beanManager.lookupBeans("NamedBeanSubThree").iterator().next().getType());


    NamedBean _defInstance = super.app.beanManager.<NamedBean>lookupBeans(NamedBean.class, _default)
        .iterator().next().getInstance();
    assertEquals("org.treblereel.injection.named.NamedBeanDefault", _defInstance.say());

    assertEquals(1,
        StreamSupport.stream(
            super.app.beanManager.<NamedBean>lookupBeans(NamedBean.class, named1).spliterator(),
            false).count());
    NamedBean _componentQualifierOne = super.app.beanManager
        .<NamedBean>lookupBeans(NamedBean.class, named1).iterator().next().getInstance();
    assertEquals("org.treblereel.injection.named.NamedBeanOne", _componentQualifierOne.say());

    assertEquals(1,
        StreamSupport
            .stream(super.app.beanManager.lookupBeans(NamedBean.class, named2).spliterator(), false)
            .count());

    NamedBean _componentQualifierTwo =
        super.app.beanManager.lookupBeans(NamedBean.class, named2).iterator().next().getInstance();
    assertEquals("org.treblereel.injection.named.NamedBeanTwo", _componentQualifierTwo.say());

    assertEquals(0,
        StreamSupport.stream(
            super.app.beanManager.lookupBeans(NamedBean.class, named1, named2).spliterator(), false)
            .count());
  }
}
