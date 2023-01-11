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

package org.treblereel.events.inheritance;

import org.treblereel.events.SimpleEvent;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

public class ObservesBean extends ObservesParent {

  public Event<SimpleEvent> event;

  @Inject
  public ObservesBean(Event<SimpleEvent> event) {
    this.event = event;
  }

  public void onEvent(@Observes SimpleEvent event) {
    events.add(event);
  }
}
