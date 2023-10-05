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

package io.crysknife.ui.templates.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventHandlerRegistration {

  private final Map<Object, List<EventHandlerHolder>> handlers = new HashMap<>();

  public void add(Object instance, EventHandlerHolder handler) {
    handlers.computeIfAbsent(instance, k -> new ArrayList<>()).add(handler.attach());
  }

  public void remove(Object key, EventHandlerHolder handler) {
    if (handlers.containsKey(key)) {
      handlers.get(key).remove(handler.remove());
    }
  }

  public void clear(Object instance) {
    if (instance == null || !handlers.containsKey(instance)) {
      return;
    }
    List<EventHandlerHolder> handlers = this.handlers.get(instance);
    for (EventHandlerHolder eventHandlerHolder : handlers) {
      eventHandlerHolder.remove();
    }
    handlers.clear();
    this.handlers.remove(instance);
  }

}
