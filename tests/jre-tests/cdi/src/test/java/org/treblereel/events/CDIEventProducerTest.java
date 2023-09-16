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

package org.treblereel.events;

import org.junit.Test;
import org.treblereel.AbstractTest;
import org.treblereel.events.inheritance.ObservesBean;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/12/21
 */
public class CDIEventProducerTest extends AbstractTest {

  @Test
  public void testSimpleEvents1() {
    CDIEventProducer cdiEventProducer =
        app.beanManager.lookupBean(CDIEventProducer.class).getInstance();
    cdiEventProducer.simpleEventEvent.fire(new SimpleEvent());
    assertEquals(1, cdiEventProducer.events.size());
  }

  @Test
  public void testSimpleEvents2() {
    app.beanManager.lookupBean(SimpleEventSubscriberApplicationScoped.class)
            .getInstance().events.clear();
    Set<SimpleEvent> events = app.beanManager.lookupBean(SimpleEventSubscriberApplicationScoped.class)
            .getInstance().events;

    SimpleEventSubscriberDependent simpleEventSubscriberDependent =
        app.beanManager.lookupBean(SimpleEventSubscriberDependent.class).getInstance();

    CDIEventProducer cdiEventProducer =
        app.beanManager.lookupBean(CDIEventProducer.class).getInstance();
    cdiEventProducer.simpleEventEvent.fire(new SimpleEvent());
    assertEquals(1, events.size());
    assertEquals(1, simpleEventSubscriberDependent.events.size());

    app.beanManager.destroyBean(app.beanManager.lookupBean(SimpleEventSubscriberApplicationScoped.class)
            .getInstance());
    app.beanManager.destroyBean(simpleEventSubscriberDependent);
    cdiEventProducer.simpleEventEvent.fire(new SimpleEvent());
    assertEquals(1, events.size());
    assertEquals(1, simpleEventSubscriberDependent.events.size());
  }

  @Test
  public void testSimpleEvents3() {
    PersonEventHolder holder = app.beanManager.lookupBean(PersonEventHolder.class).getInstance();
    CDIEventProducer cdiEventProducer =
        app.beanManager.lookupBean(CDIEventProducer.class).getInstance();
    cdiEventProducer.managerEvent.fire(new PersonEvent(new Manager()));
    assertEquals(1, holder.events.size());
  }

  @Test
  public void testSimpleEvents4() {
    Set<PersonEvent> events = new HashSet<>();
    TesterWithDependentEventListener holder = app.beanManager.lookupBean(TesterWithDependentEventListener.class).getInstance();
    holder.holder.events = events;
    CDIEventProducer cdiEventProducer =
            app.beanManager.lookupBean(CDIEventProducer.class).getInstance();
    cdiEventProducer.managerEvent.fire(new PersonEvent(new Manager()));
    assertEquals(1, events.size());
    app.beanManager.destroyBean(holder.holder);
    cdiEventProducer.managerEvent.fire(new PersonEvent(new Manager()));
    assertEquals(1, events.size());
  }

  @Test
  public void testSimpleEvents5() {
    Set<PersonEvent> events = new HashSet<>();
    TesterWithSingletonEventListener holder = app.beanManager.lookupBean(TesterWithSingletonEventListener.class).getInstance();
    holder.holder.events = events;
    CDIEventProducer cdiEventProducer =
            app.beanManager.lookupBean(CDIEventProducer.class).getInstance();
    cdiEventProducer.managerEvent.fire(new PersonEvent(new Manager()));
    assertEquals(1, events.size());
    app.beanManager.destroyBean(holder.holder);
    cdiEventProducer.managerEvent.fire(new PersonEvent(new Manager()));
    assertEquals(1, events.size());
  }

  @Test
  public void testInheritance() {
    ObservesBean holder = app.beanManager.lookupBean(ObservesBean.class).getInstance();
    holder.events.clear();
    holder.event.fire(new SimpleEvent());
    assertEquals(3, holder.events.size());
  }
}
