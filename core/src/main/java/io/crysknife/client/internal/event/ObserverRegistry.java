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

package io.crysknife.client.internal.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ObserverRegistry {

  public static final ObserverRegistry INSTANCE = new ObserverRegistry();

  // eventType -> (ownerInstance -> callbacks)
  private final Map<Class<?>, Map<Object, List<Consumer<Object>>>> observers = new HashMap<>();

  public void subscribe(Class<?> eventType, Object ownerInstance, Consumer<Object> callback) {
    observers
        .computeIfAbsent(eventType, k -> new LinkedHashMap<>())
        .computeIfAbsent(ownerInstance, k -> new ArrayList<>())
        .add(callback);
  }

  public void unsubscribe(Class<?> eventType, Object ownerInstance) {
    Map<Object, List<Consumer<Object>>> byOwner = observers.get(eventType);
    if (byOwner != null) {
      byOwner.remove(ownerInstance);
      if (byOwner.isEmpty()) {
        observers.remove(eventType);
      }
    }
  }

  public void unsubscribeAll(Object ownerInstance) {
    for (Map<Object, List<Consumer<Object>>> byOwner : observers.values()) {
      byOwner.remove(ownerInstance);
    }
    observers.values().removeIf(Map::isEmpty);
  }

  public void fire(Class<?> eventType, Object event) {
    List<Class<?>> hierarchy = getTypeHierarchy(eventType);
    for (Class<?> type : hierarchy) {
      Map<Object, List<Consumer<Object>>> byOwner = observers.get(type);
      if (byOwner != null) {
        List<Consumer<Object>> snapshot = new ArrayList<>();
        for (List<Consumer<Object>> callbacks : byOwner.values()) {
          snapshot.addAll(callbacks);
        }
        for (Consumer<Object> callback : snapshot) {
          callback.accept(event);
        }
      }
    }
  }

  private List<Class<?>> getTypeHierarchy(Class<?> type) {
    List<Class<?>> hierarchy = new ArrayList<>();
    Class<?> current = type;
    while (current != null && current != Object.class) {
      hierarchy.add(current);
      current = current.getSuperclass();
    }
    return hierarchy;
  }
}
