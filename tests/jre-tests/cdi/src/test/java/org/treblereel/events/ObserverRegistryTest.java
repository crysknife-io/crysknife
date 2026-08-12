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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.crysknife.client.internal.event.ObserverRegistry;

public class ObserverRegistryTest {

  private ObserverRegistry registry;

  @Before
  public void setUp() {
    registry = new ObserverRegistry();
  }

  @Test
  public void testSubscribeAndFire() {
    List<SimpleEvent> received = new ArrayList<>();
    Object owner = new Object();

    registry.subscribe(SimpleEvent.class, owner, e -> received.add((SimpleEvent) e));
    registry.fire(SimpleEvent.class, new SimpleEvent());

    assertEquals(1, received.size());
  }

  @Test
  public void testUnsubscribe() {
    List<SimpleEvent> received = new ArrayList<>();
    Object owner = new Object();

    registry.subscribe(SimpleEvent.class, owner, e -> received.add((SimpleEvent) e));
    registry.fire(SimpleEvent.class, new SimpleEvent());
    assertEquals(1, received.size());

    registry.unsubscribe(SimpleEvent.class, owner);
    registry.fire(SimpleEvent.class, new SimpleEvent());
    assertEquals(1, received.size());
  }

  @Test
  public void testMultipleObserversSameType() {
    List<String> log = new ArrayList<>();
    Object owner1 = new Object();
    Object owner2 = new Object();

    registry.subscribe(SimpleEvent.class, owner1, e -> log.add("first"));
    registry.subscribe(SimpleEvent.class, owner2, e -> log.add("second"));
    registry.fire(SimpleEvent.class, new SimpleEvent());

    assertEquals(2, log.size());
    assertTrue(log.contains("first"));
    assertTrue(log.contains("second"));
  }

  @Test
  public void testTypeHierarchyDispatch() {
    List<String> log = new ArrayList<>();
    Object owner = new Object();

    // Observer for parent type
    registry.subscribe(SimpleEvent.class, owner, e -> log.add("parent"));

    // Fire a child event type — the parent observer should also be notified
    // (This test uses SimpleEvent directly; Task 5 will add a full hierarchy test)
    registry.fire(SimpleEvent.class, new SimpleEvent());

    assertEquals(1, log.size());
  }

  @Test
  public void testNoSubscribers() {
    // Should not throw
    registry.fire(SimpleEvent.class, new SimpleEvent());
  }

  @Test
  public void testMultipleCallbacksSameOwner() {
    List<String> log = new ArrayList<>();
    Object owner = new Object();

    registry.subscribe(SimpleEvent.class, owner, e -> log.add("cb1"));
    registry.subscribe(SimpleEvent.class, owner, e -> log.add("cb2"));
    registry.fire(SimpleEvent.class, new SimpleEvent());

    assertEquals(2, log.size());
  }

  @Test
  public void testUnsubscribeRemovesAllCallbacksForOwner() {
    List<String> log = new ArrayList<>();
    Object owner = new Object();

    registry.subscribe(SimpleEvent.class, owner, e -> log.add("cb1"));
    registry.subscribe(SimpleEvent.class, owner, e -> log.add("cb2"));
    registry.unsubscribe(SimpleEvent.class, owner);
    registry.fire(SimpleEvent.class, new SimpleEvent());

    assertEquals(0, log.size());
  }

  @Test
  public void testUnsubscribeAllRemovesAcrossEventTypes() {
    List<String> log = new ArrayList<>();
    Object owner = new Object();

    registry.subscribe(SimpleEvent.class, owner, e -> log.add("simple"));
    registry.subscribe(ChildEvent.class, owner, e -> log.add("child"));

    registry.unsubscribeAll(owner);

    registry.fire(SimpleEvent.class, new SimpleEvent());
    registry.fire(ChildEvent.class, new ChildEvent());

    assertEquals(0, log.size());
  }

  @Test
  public void testUnsubscribeAllLeavesOtherOwners() {
    List<String> log = new ArrayList<>();
    Object owner1 = new Object();
    Object owner2 = new Object();

    registry.subscribe(SimpleEvent.class, owner1, e -> log.add("owner1"));
    registry.subscribe(SimpleEvent.class, owner2, e -> log.add("owner2"));

    registry.unsubscribeAll(owner1);
    registry.fire(SimpleEvent.class, new SimpleEvent());

    assertEquals(1, log.size());
    assertEquals("owner2", log.get(0));
  }

  @Test
  public void testFireDoesNotThrowWhenCallbackUnsubscribes() {
    List<String> log = new ArrayList<>();
    Object owner = new Object();

    registry.subscribe(SimpleEvent.class, owner, e -> {
      log.add("first");
      registry.unsubscribe(SimpleEvent.class, owner);
    });

    registry.fire(SimpleEvent.class, new SimpleEvent());
    assertEquals(1, log.size());

    log.clear();
    registry.fire(SimpleEvent.class, new SimpleEvent());
    assertEquals(0, log.size());
  }

  @Test
  public void testFireDoesNotThrowWhenCallbackSubscribes() {
    List<String> log = new ArrayList<>();
    Object owner1 = new Object();
    Object owner2 = new Object();

    registry.subscribe(SimpleEvent.class, owner1, e -> {
      log.add("first");
      registry.subscribe(SimpleEvent.class, owner2, e2 -> log.add("added"));
    });

    registry.fire(SimpleEvent.class, new SimpleEvent());
    assertEquals(1, log.size());
    assertEquals("first", log.get(0));
  }

  static class ChildEvent extends SimpleEvent {
  }
}
