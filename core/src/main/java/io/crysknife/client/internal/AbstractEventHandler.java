/*
 * Copyright © 2020 Treblereel
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
package io.crysknife.client.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;

import elemental2.dom.DomGlobal;
import io.crysknife.client.InstanceFactory;
import jakarta.enterprise.event.Event;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/1/19
 */
public abstract class AbstractEventHandler<T, I> implements Event<T> {

  private final Map<I, Set<BiConsumer<T, I>>> subscribers = new HashMap<>();

  private final Queue<Pair> factories = new LinkedList<>();

  public void fire(T t) {
    Pair pair = factories.poll();
    while (pair != null) {
      addSubscriber(pair.instance.getInstance(), pair.subscriber);
      pair = factories.poll();
    }
    subscribers.forEach((instance, subscribers) -> subscribers
        .forEach(subscriber -> subscriber.accept(t, instance)));
  }

  public void addSubscriber(InstanceFactory<I> instance, BiConsumer<T, I> subscriber) {
    Pair pair = new Pair(instance, subscriber);
    factories.add(pair);
  }

  public void addSubscriber(I instance, BiConsumer<T, I> subscriber) {
    if (!subscribers.containsKey(instance)) {
      subscribers.put(instance, new HashSet<>());
    }
    if (!subscribers.get(instance).contains(subscriber)) {
      subscribers.get(instance).add(subscriber);
    }
  }

  public void removeSubscriber(I instance, BiConsumer<T, I> subscriber) {
    if (subscribers.containsKey(instance)) {
      subscribers.get(instance).remove(subscriber);
      if (subscribers.get(instance).isEmpty()) {
        subscribers.remove(instance);
      }
    }
  }

  private class Pair {

    private final InstanceFactory<I> instance;
    private final BiConsumer<T, I> subscriber;

    public Pair(InstanceFactory<I> instance, BiConsumer<T, I> subscriber) {
      this.instance = instance;
      this.subscriber = subscriber;
    }
  }
}
