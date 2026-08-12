/*
 * Copyright © 2023 Dmitrii Tikhomirov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.treblereel.events.hierarchy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.treblereel.AbstractTest;

public class TypeHierarchyEventTest extends AbstractTest {

  @Test
  public void testChildEventNotifiesApplicationScopedBaseObserver() {
    BaseEventObserver observer =
        app.beanManager.lookupBean(BaseEventObserver.class).getInstance();
    observer.events.clear();

    ChildEventProducer producer =
        app.beanManager.lookupBean(ChildEventProducer.class).getInstance();
    producer.childEvent.fire(new ChildEvent("hello"));

    assertEquals(1, observer.events.size());
    assertTrue(observer.events.get(0) instanceof ChildEvent);
    assertEquals("hello", observer.events.get(0).message);
  }

  @Test
  public void testChildEventNotifiesDependentBaseObserver() {
    DependentBaseEventObserver observer =
        app.beanManager.lookupBean(DependentBaseEventObserver.class).getInstance();

    ChildEventProducer producer =
        app.beanManager.lookupBean(ChildEventProducer.class).getInstance();
    producer.childEvent.fire(new ChildEvent("dependent"));

    assertEquals(1, observer.events.size());
    assertTrue(observer.events.get(0) instanceof ChildEvent);
    assertEquals("dependent", observer.events.get(0).message);
  }

  @Test
  public void testDestroyApplicationScopedStopsHierarchyDispatch() {
    BaseEventObserver observer =
        app.beanManager.lookupBean(BaseEventObserver.class).getInstance();
    observer.events.clear();

    ChildEventProducer producer =
        app.beanManager.lookupBean(ChildEventProducer.class).getInstance();
    producer.childEvent.fire(new ChildEvent("before"));
    assertEquals(1, observer.events.size());

    app.beanManager.destroyBean(observer);
    producer.childEvent.fire(new ChildEvent("after"));
    assertEquals(1, observer.events.size());
  }

  @Test
  public void testDestroyDependentStopsHierarchyDispatch() {
    DependentBaseEventObserver observer =
        app.beanManager.lookupBean(DependentBaseEventObserver.class).getInstance();

    ChildEventProducer producer =
        app.beanManager.lookupBean(ChildEventProducer.class).getInstance();
    producer.childEvent.fire(new ChildEvent("before"));
    assertEquals(1, observer.events.size());

    app.beanManager.destroyBean(observer);
    producer.childEvent.fire(new ChildEvent("after"));
    assertEquals(1, observer.events.size());
  }

  @Test
  public void testBothScopesReceiveChildEvent() {
    BaseEventObserver appScopedObserver =
        app.beanManager.lookupBean(BaseEventObserver.class).getInstance();
    appScopedObserver.events.clear();

    DependentBaseEventObserver dependentObserver =
        app.beanManager.lookupBean(DependentBaseEventObserver.class).getInstance();

    ChildEventProducer producer =
        app.beanManager.lookupBean(ChildEventProducer.class).getInstance();
    producer.childEvent.fire(new ChildEvent("both"));

    assertEquals(1, appScopedObserver.events.size());
    assertEquals(1, dependentObserver.events.size());
    assertEquals("both", appScopedObserver.events.get(0).message);
    assertEquals("both", dependentObserver.events.get(0).message);
  }
}
