/*
 * Copyright © 2024 Treblereel
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.crysknife.client.internal.event.EventManager;
import org.junit.Test;
import org.treblereel.AbstractTest;

public class LazyActivationTest extends AbstractTest {

    private EventManager getEventManager() {
        return app.beanManager.lookupBean(EventManager.class).getInstance();
    }

    @Test
    public void testSingletonObserverActivatedOnEvent() {
        EventManager eventManager = getEventManager();

        eventManager.get(LazyActivationEvent.class).fire(new LazyActivationEvent("test"));

        LazyActivationSingletonObserver observer =
                app.beanManager.lookupBean(LazyActivationSingletonObserver.class).getInstance();
        assertNotNull(observer);
        assertTrue(observer.receivedEvents.size() >= 1);
        assertTrue(observer.receivedEvents.stream()
                .anyMatch(e -> "test".equals(e.getPayload())));
    }

    @Test
    public void testApplicationScopedObserverActivatedOnEvent() {
        EventManager eventManager = getEventManager();

        eventManager.get(LazyActivationEvent.class).fire(new LazyActivationEvent("appscoped"));

        LazyActivationAppScopedObserver observer =
                app.beanManager.lookupBean(LazyActivationAppScopedObserver.class).getInstance();
        assertNotNull(observer);
        assertTrue(observer.receivedEvents.size() >= 1);
        assertTrue(observer.receivedEvents.stream()
                .anyMatch(e -> "appscoped".equals(e.getPayload())));
    }

    @Test
    public void testSingletonReceivesEventsAfterExplicitInstantiation() {
        LazyActivationSingletonObserver observer =
                app.beanManager.lookupBean(LazyActivationSingletonObserver.class).getInstance();
        int before = observer.receivedEvents.size();

        EventManager eventManager = getEventManager();
        eventManager.get(LazyActivationEvent.class).fire(new LazyActivationEvent("after-init"));

        assertEquals(before + 1, observer.receivedEvents.size());
        assertEquals("after-init", observer.receivedEvents.get(observer.receivedEvents.size() - 1).getPayload());
    }

    @Test
    public void testMultipleEventsReceivedBySingleton() {
        LazyActivationSingletonObserver observer =
                app.beanManager.lookupBean(LazyActivationSingletonObserver.class).getInstance();
        int before = observer.receivedEvents.size();

        EventManager eventManager = getEventManager();
        eventManager.get(LazyActivationEvent.class).fire(new LazyActivationEvent("first"));
        eventManager.get(LazyActivationEvent.class).fire(new LazyActivationEvent("second"));

        assertEquals(before + 2, observer.receivedEvents.size());
    }

    @Test
    public void testDependentObserverNotActivatedLazily() {
        EventManager eventManager = getEventManager();

        eventManager.get(LazyActivationEvent.class).fire(new LazyActivationEvent("should-miss"));

        LazyActivationDependentObserver observer =
                app.beanManager.lookupBean(LazyActivationDependentObserver.class).getInstance();
        assertTrue(observer.receivedEvents.isEmpty());
    }

    @Test
    public void testMultiObserverSingletonActivatedByFirstEventType() {
        EventManager eventManager = getEventManager();

        eventManager.get(LazyActivationEvent.class).fire(new LazyActivationEvent("evt1"));

        LazyActivationMultiObserverSingleton observer =
                app.beanManager.lookupBean(LazyActivationMultiObserverSingleton.class).getInstance();
        assertNotNull(observer);
        assertTrue(observer.firstEvents.size() >= 1);
        assertTrue(observer.firstEvents.stream()
                .anyMatch(e -> "evt1".equals(e.getPayload())));
    }

    @Test
    public void testMultiObserverSingletonActivatedBySecondEventType() {
        EventManager eventManager = getEventManager();

        eventManager.get(LazyActivationSecondEvent.class).fire(new LazyActivationSecondEvent("evt2"));

        LazyActivationMultiObserverSingleton observer =
                app.beanManager.lookupBean(LazyActivationMultiObserverSingleton.class).getInstance();
        assertNotNull(observer);
        assertTrue(observer.secondEvents.size() >= 1);
        assertTrue(observer.secondEvents.stream()
                .anyMatch(e -> "evt2".equals(e.getPayload())));
    }

    @Test
    public void testMultiObserverSingletonReceivesBothEventTypes() {
        LazyActivationMultiObserverSingleton observer =
                app.beanManager.lookupBean(LazyActivationMultiObserverSingleton.class).getInstance();
        int beforeFirst = observer.firstEvents.size();
        int beforeSecond = observer.secondEvents.size();

        EventManager eventManager = getEventManager();
        eventManager.get(LazyActivationEvent.class).fire(new LazyActivationEvent("both-first"));
        eventManager.get(LazyActivationSecondEvent.class).fire(new LazyActivationSecondEvent("both-second"));

        assertEquals(beforeFirst + 1, observer.firstEvents.size());
        assertEquals(beforeSecond + 1, observer.secondEvents.size());
    }
}
