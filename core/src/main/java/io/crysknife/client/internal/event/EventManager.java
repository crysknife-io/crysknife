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

package io.crysknife.client.internal.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import jakarta.ejb.Startup;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.crysknife.client.BeanManager;
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.internal.BeanFactory;

@Startup
@Singleton
public class EventManager {

  private final ObserverRegistry registry = ObserverRegistry.INSTANCE;
  private final Map<Class<?>, EventImpl<?>> events = new HashMap<>();

  @Inject
  public EventManager(BeanManager beanManager) {
  }

  public ObserverRegistry getRegistry() {
    return registry;
  }

  @SuppressWarnings("unchecked")
  public <T> Event<T> get(Class<?> type) {
    return (Event<T>) events.computeIfAbsent(type, t -> new EventImpl<>(t, registry));
  }

  public static class EventManagerFactory extends BeanFactory<EventManager> {

    private final Supplier<InstanceFactory<BeanManager>> _constructor_beanManager =
        () -> beanManager.lookupBean(io.crysknife.client.BeanManager.class);

    public EventManagerFactory(BeanManager beanManager) {
      super(beanManager);
    }

    @Override()
    public EventManager getInstance() {
      if (instance != null) {
        return instance;
      }
      EventManager instance = createInstanceInternal();
      initInstance(instance);
      return instance;
    }

    @Override()
    public EventManager createInstance() {
      if (this.instance != null) {
        return instance;
      }
      instance = new EventManager(this._constructor_beanManager.get().getInstance());
      return instance;
    }
  }
}
