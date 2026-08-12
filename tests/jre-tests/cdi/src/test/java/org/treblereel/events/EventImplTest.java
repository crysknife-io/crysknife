/*
 * Copyright © 2023 Treblereel
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.crysknife.client.internal.event.EventImpl;
import io.crysknife.client.internal.event.ObserverRegistry;
import jakarta.enterprise.event.Event;

public class EventImplTest {

  private ObserverRegistry registry;

  @Before
  public void setUp() {
    registry = new ObserverRegistry();
  }

  @Test
  public void testFireDelegatesToRegistry() {
    List<SimpleEvent> received = new ArrayList<>();
    Object owner = new Object();
    registry.subscribe(SimpleEvent.class, owner, e -> received.add((SimpleEvent) e));

    Event<SimpleEvent> event = new EventImpl<>(SimpleEvent.class, registry);
    event.fire(new SimpleEvent());

    assertEquals(1, received.size());
  }

  @Test
  public void testMultipleFires() {
    List<SimpleEvent> received = new ArrayList<>();
    Object owner = new Object();
    registry.subscribe(SimpleEvent.class, owner, e -> received.add((SimpleEvent) e));

    Event<SimpleEvent> event = new EventImpl<>(SimpleEvent.class, registry);
    event.fire(new SimpleEvent());
    event.fire(new SimpleEvent());

    assertEquals(2, received.size());
  }
}
