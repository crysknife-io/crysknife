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

package org.treblereel.injection.managedinstance;

import io.crysknife.client.ManagedInstance;
import io.crysknife.client.internal.IOCResolutionException;
import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.injection.dependent.SimpleBeanDependent;
import org.treblereel.injection.inheritance.BeanChild;
import org.treblereel.injection.managedinstance.any.DefaultPreferencesRegistry;
import org.treblereel.injection.managedinstance.any.StunnerPreferencesRegistryLoader;
import org.treblereel.injection.managedinstance.inheritance.Child;
import org.treblereel.injection.managedinstance.select.ManagedInstanceBeanHolder;
import org.treblereel.injection.managedinstance.select.SimpleInterface;
import org.treblereel.injection.managedinstance.typed.AbstractTypedBeanHolder;
import org.treblereel.injection.managedinstance.typed.SimpleBeanAbstractTyped;
import org.treblereel.produces.qualifier.QualifierBean;

import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Named;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/25/21
 */
public class ManagedInstanceBeanTest extends AbstractTest {

  private final ComponentQualifierOne componentQualifierOne = new ComponentQualifierOne() {

    @Override
    public Class<? extends Annotation> annotationType() {
      return ComponentQualifierOne.class;
    }
  };

  private final ComponentQualifierTwo componentQualifierTwo = new ComponentQualifierTwo() {

    @Override
    public Class<? extends Annotation> annotationType() {
      return ComponentQualifierTwo.class;
    }
  };

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

    ComponentIface componentTwo = super.app.beanManager
        .<ComponentIface>lookupBean(ComponentIface.class, componentQualifierTwo).getInstance();

    assertEquals("ComponentTwo", componentTwo.getComponentName());
  }

  @Test
  public void testInstance() {
    ManagedInstance<ComponentIface> managedInstanceBean =
        app.getManagedInstanceBean().getInstanceBean();

    assertNotNull(managedInstanceBean);

    List<ComponentIface> actualList =
        StreamSupport.stream(managedInstanceBean.spliterator(), false).collect(Collectors.toList());
    assertEquals(3, actualList.size());

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

    ComponentIface componentTwo = super.app.beanManager
        .<ComponentIface>lookupBean(ComponentIface.class, componentQualifierTwo).getInstance();
    assertEquals("ComponentTwo", componentTwo.getComponentName());
  }

  @Test
  public void testInstance2() {
    Instance<SimpleBeanDependent> managedInstanceBean = app.getManagedInstanceBean().getBean();
    assertEquals(SimpleBeanDependent.class.getSimpleName(), managedInstanceBean.get().getName());
  }

  @Test
  public void testInstanceProducerBean() {
    Instance<QualifierBean> managedInstanceBean = app.getManagedInstanceBean().getBean2();
    assertEquals("Default", managedInstanceBean.get().say());
  }

  @Test
  public void testSimpleBean1() {
    assertEquals(SimpleBean.class.getCanonicalName(),
        app.getManagedInstanceBean().simpleBean1.get().say());

    assertEquals(SimpleBean.class.getCanonicalName(),
        app.getManagedInstanceBean().constructor_simpleBean1.get().say());
  }

  @Test
  public void testSimpleBean2() {
    assertEquals(SimpleBean.class.getCanonicalName(),
        app.getManagedInstanceBean().simpleBean2.get().say());

    assertEquals(SimpleBean.class.getCanonicalName(),
        app.getManagedInstanceBean().constructor_simpleBean2.get().say());
  }

  @Test
  public void isUnsatisfied() {

    ManagedInstanceTestsHolder managedInstanceTestsHolder =
        app.beanManager.lookupBean(ManagedInstanceTestsHolder.class).getInstance();

    assertTrue(managedInstanceTestsHolder.uselessInterfaces.isUnsatisfied());
    assertFalse(managedInstanceTestsHolder.componentIface.isUnsatisfied());
    assertTrue(managedInstanceTestsHolder.simpleBean.select(new Default() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Default.class;
      }
    }).isUnsatisfied());
  }

  @Test
  public void testIsAmbiguous() {
    ManagedInstanceTestsHolder managedInstanceTestsHolder =
        app.beanManager.lookupBean(ManagedInstanceTestsHolder.class).getInstance();

    assertFalse(managedInstanceTestsHolder.uselessInterfaces.isAmbiguous());
    assertTrue(managedInstanceTestsHolder.componentIface.isAmbiguous());
    assertFalse(managedInstanceTestsHolder.simpleBean.select(new Default() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Default.class;
      }
    }).isAmbiguous());
  }

  @Test
  public void checkIOCResolutionException() {
    IOCResolutionException exception = assertThrows(IOCResolutionException.class, () -> {
      app.beanManager.lookupBean(SimpleBean.class,
          new org.treblereel.injection.managedinstance.ComponentQualifierTwo() {

            @Override
            public Class<? extends Annotation> annotationType() {
              return org.treblereel.injection.managedinstance.ComponentQualifierTwo.class;
            }
          }).getInstance();
    });
    assertEquals(
        "No beans matched org.treblereel.injection.managedinstance.SimpleBean with qualifiers { org.treblereel.injection.managedinstance.ComponentQualifierTwo }",
        exception.getMessage());
  }

  @Test
  public void checkIOCResolutionExceptionInManagedInstance() {
    ManagedInstanceTestsHolder managedInstanceTestsHolder =
        app.beanManager.lookupBean(ManagedInstanceTestsHolder.class).getInstance();

    IOCResolutionException exception = assertThrows(IOCResolutionException.class, () -> {
      managedInstanceTestsHolder.simpleBean.select(new ComponentQualifierTwo() {

        @Override
        public Class<? extends Annotation> annotationType() {
          return ComponentQualifierTwo.class;
        }
      }).get();
    });
    assertEquals(
        "No beans matched org.treblereel.injection.managedinstance.SimpleBean with qualifiers { org.treblereel.injection.managedinstance.ComponentQualifierTwo }",
        exception.getMessage());
  }

  @Test
  public void testSelect() {
    ManagedInstanceBeanHolder managedInstanceBeanHolder =
        app.beanManager.lookupBean(ManagedInstanceBeanHolder.class).getInstance();
    ManagedInstance<SimpleInterface> instance = managedInstanceBeanHolder.instance;
    assertTrue(instance.isAmbiguous());

    assertEquals(org.treblereel.injection.managedinstance.select.SimpleBean.class,
        app.beanManager.lookupBean(SimpleInterface.class,
            new org.treblereel.injection.managedinstance.select.ComponentQualifierOne() {

              @Override
              public Class<? extends Annotation> annotationType() {
                return org.treblereel.injection.managedinstance.select.ComponentQualifierOne.class;
              }
            }).getInstance().getClass());

    assertFalse(instance
        .select(new org.treblereel.injection.managedinstance.select.ComponentQualifierOne() {

          @Override
          public Class<? extends Annotation> annotationType() {
            return org.treblereel.injection.managedinstance.select.ComponentQualifierOne.class;
          }
        }).isAmbiguous());

    assertEquals(org.treblereel.injection.managedinstance.select.SimpleBeanTwo.class,
        app.beanManager.lookupBean(SimpleInterface.class,
            new org.treblereel.injection.managedinstance.select.ComponentQualifierTwo() {

              @Override
              public Class<? extends Annotation> annotationType() {
                return org.treblereel.injection.managedinstance.select.ComponentQualifierTwo.class;
              }
            }).getInstance().getClass());

    assertFalse(instance
        .select(new org.treblereel.injection.managedinstance.select.ComponentQualifierTwo() {

          @Override
          public Class<? extends Annotation> annotationType() {
            return org.treblereel.injection.managedinstance.select.ComponentQualifierTwo.class;
          }
        }).isAmbiguous());

    assertFalse(instance.select(org.treblereel.injection.managedinstance.select.SimpleBeanTwo.class,
        new org.treblereel.injection.managedinstance.select.ComponentQualifierTwo() {

          @Override
          public Class<? extends Annotation> annotationType() {
            return org.treblereel.injection.managedinstance.select.ComponentQualifierTwo.class;
          }
        }).isAmbiguous());


    assertEquals(org.treblereel.injection.managedinstance.select.SimpleBeanTwo.class,
        app.beanManager
            .lookupBean(org.treblereel.injection.managedinstance.select.SimpleBeanTwo.class,
                new Default() {

                  @Override
                  public Class<? extends Annotation> annotationType() {
                    return Default.class;
                  }
                })
            .getInstance().getClass());


    assertFalse(instance
        .select(org.treblereel.injection.managedinstance.select.SimpleBeanTwo.class, new Default() {

          @Override
          public Class<? extends Annotation> annotationType() {
            return Default.class;
          }
        }).isAmbiguous());

  }

  @Test
  public void testAny() {
    assertEquals(DefaultPreferencesRegistry.class,
        app.beanManager.lookupBean(StunnerPreferencesRegistryLoader.class)
            .getInstance().preferencesHolders.get().getClass());
  }

  @Test
  public void testDependent() {
    assertNotEquals(
        app.beanManager.lookupBean(SimpleDependentBeanHolder.class).getInstance().bean1.get(),
        app.beanManager.lookupBean(SimpleDependentBeanHolder.class).getInstance().bean1.get());
    assertNotEquals(
        app.beanManager.lookupBean(SimpleDependentBeanHolder.class).getInstance().bean1.get(),
        app.beanManager.lookupBean(SimpleDependentBeanHolder.class).getInstance().bean2.get());
    assertNotEquals(
        app.beanManager.lookupBean(SimpleDependentBeanHolder.class).getInstance().bean1.get().id,
        app.beanManager.lookupBean(SimpleDependentBeanHolder.class).getInstance().bean2.get().id);
  }

  @Test
  public void testQualifiedManagedInstance() {
    QualifiedManagedInstanceTestsHolder holder =
        app.beanManager.lookupBean(QualifiedManagedInstanceTestsHolder.class).getInstance();
    assertEquals("ComponentOne", holder.c_bean1.get().getComponentName());
    assertEquals("ComponentTwo", holder.c_bean2.get().getComponentName());
    assertEquals("ComponentOne", holder.f_bean1.get().getComponentName());
    assertEquals("ComponentTwo", holder.f_bean2.get().getComponentName());

  }

  @Test
  public void testNamedManagedInstance() {
    NamedManagedInstanceTestsHolder holder =
        app.beanManager.lookupBean(NamedManagedInstanceTestsHolder.class).getInstance();
    assertEquals("NamedBeanOne", holder.c_bean1.get().getComponentName());
    assertEquals("NamedBeanTwo", holder.c_bean2.get().getComponentName());
    assertEquals("NamedBeanOne", holder.f_bean1.get().getComponentName());
    assertEquals("NamedBeanTwo", holder.f_bean2.get().getComponentName());

  }

  @Test
  public void testTypedAndDefaultManagedInstance() {
    AbstractTypedBeanHolder holder =
        app.beanManager.lookupBean(AbstractTypedBeanHolder.class).getInstance();
    assertEquals(SimpleBeanAbstractTyped.class, holder.instance.get().getClass());

  }

  @Test
  public void testInheritance() {
    Child holder = app.beanManager.lookupBean(Child.class).getInstance();
    assertEquals(SimpleBean.class, holder.get().getClass());
    assertEquals(BeanChild.class, holder.get2().getClass());
  }
}
