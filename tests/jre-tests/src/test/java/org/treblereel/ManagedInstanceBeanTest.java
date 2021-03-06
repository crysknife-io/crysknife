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

import javax.inject.Named;

import io.crysknife.client.ManagedInstance;
import org.junit.Assert;
import org.junit.Test;
import org.treblereel.injection.managedinstance.ComponentIface;
import org.treblereel.injection.managedinstance.ComponentQualifierOne;
import org.treblereel.injection.managedinstance.ComponentQualifierTwo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/25/21
 */
public class ManagedInstanceBeanTest extends AbstractTest {

  @Test
  public void testPostConstructAppBootstrap() {
    ManagedInstance<ComponentIface> managedInstanceBean =
        app.getManagedInstanceBean().getManagedInstanceBean();

    assertNotNull(managedInstanceBean);

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

    Named named1 = new Named() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Named.class;
      }

      @Override
      public String value() {
        return "one";
      }
    };

    Named named11 = new Named() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Named.class;
      }

      @Override
      public String value() {
        return "one";
      }
    };

    Named named2 = new Named() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Named.class;
      }

      @Override
      public String value() {
        return "two";
      }
    };

    ComponentIface componentTwo =
        super.app.beanManager.lookupBean(ComponentIface.class, componentQualifierTwo).get();

    assertEquals("ComponentTwo", componentTwo.getComponentName());
  }
}
