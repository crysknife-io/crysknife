/*
 * Copyright © 2025 Treblereel
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
package io.crysknife.ui.databinding.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import io.crysknife.ui.databinding.api.Converter;
import io.crysknife.ui.databinding.api.ListComponent;
import io.crysknife.ui.databinding.api.PropertyChangeEvent;
import io.crysknife.ui.databinding.api.PropertyChangeHandler;
import io.crysknife.ui.databinding.api.PropertyChangeUnsubscribeHandle;

/**
 * Manages all bindings for a single model proxy. Responsible for:
 * <ul>
 *   <li>Maintaining the map of property → bound elements</li>
 *   <li>Listening for DOM events and propagating UI → model changes</li>
 *   <li>Pushing model → UI changes when properties are set via the proxy</li>
 *   <li>Firing {@link PropertyChangeHandler}s on property changes</li>
 *   <li>Supporting dot-notation for nested {@code @Bindable} bean properties</li>
 * </ul>
 *
 * @author Dmitrii Tikhomirov
 */
public class BindingAgent {

  private final BindableProxy<?> proxy;
  private final BiConsumer<String, Object> propertySetter;
  private final Map<String, List<Binding>> bindings = new HashMap<>();
  private final List<HandlerRegistration> globalHandlers = new ArrayList<>();
  private final Map<String, List<HandlerRegistration>> propertyHandlers = new HashMap<>();
  private final Map<String, List<NestedBindingInfo>> nestedBindingsMap = new HashMap<>();
  private final Map<String, ListComponent<?>> listBindings = new HashMap<>();
  private boolean suppressEvents;

  /**
   * @param proxy the bindable proxy this agent is attached to
   * @param propertySetter a function that sets a property value on the proxy by name — generated
   *     code provides a dispatch that calls the correct setter method
   */
  public BindingAgent(BindableProxy<?> proxy, BiConsumer<String, Object> propertySetter) {
    this.proxy = proxy;
    this.propertySetter = propertySetter;
  }

  public void bind(HTMLElement element, String property, Converter<?, ?> converter) {
    int dot = property.indexOf('.');
    if (dot >= 0) {
      bindNested(element, property, converter);
      return;
    }

    ElementAccessor accessor = ElementAccessors.forElement(element);
    Binding binding = new Binding(element, property, accessor, converter);

    bindings.computeIfAbsent(property, k -> new ArrayList<>()).add(binding);

    Object currentValue = proxy.getPropertyValue(property);
    binding.updateElement(currentValue);

    String eventType = accessor.getChangeEventType();
    if (eventType != null) {
      EventListener listener = evt -> {
        if (!suppressEvents) {
          Object uiValue = binding.readElement();
          suppressEvents = true;
          try {
            setModelProperty(property, uiValue);
          } finally {
            suppressEvents = false;
          }
        }
      };
      binding.setDomListener(listener);
      element.addEventListener(eventType, listener);
    }
  }

  private void bindNested(HTMLElement element, String fullPath, Converter<?, ?> converter) {
    int dot = fullPath.indexOf('.');
    String head = fullPath.substring(0, dot);
    String tail = fullPath.substring(dot + 1);

    ElementAccessor accessor = ElementAccessors.forElement(element);
    Binding binding = new Binding(element, fullPath, accessor, converter);
    bindings.computeIfAbsent(fullPath, k -> new ArrayList<>()).add(binding);

    Object currentValue = resolvePropertyValue(fullPath);
    binding.updateElement(currentValue);

    String eventType = accessor.getChangeEventType();
    if (eventType != null) {
      EventListener listener = evt -> {
        if (!suppressEvents) {
          Object uiValue = binding.readElement();
          suppressEvents = true;
          try {
            setModelProperty(fullPath, uiValue);
          } finally {
            suppressEvents = false;
          }
        }
      };
      binding.setDomListener(listener);
      element.addEventListener(eventType, listener);
    }

    NestedBindingInfo info = new NestedBindingInfo(fullPath, head, tail);
    registerNestedListener(info);
    nestedBindingsMap.computeIfAbsent(head, k -> new ArrayList<>()).add(info);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void bindList(ListComponent<?> component, String property) {
    listBindings.put(property, component);
    Object value = proxy.getPropertyValue(property);
    if (value instanceof List) {
      ((ListComponent) component).setItems((List) value);
    }
  }

  private void registerNestedListener(NestedBindingInfo info) {
    Object nested = proxy.getPropertyValue(info.head);
    if (nested instanceof BindableProxy) {
      BindingAgent nestedAgent = ((BindableProxy<?>) nested).getBindingAgent();
      info.nestedHandle = nestedAgent.addPropertyChangeHandler(info.tail, event -> {
        List<Binding> bound = bindings.get(info.fullPath);
        if (bound != null && !suppressEvents) {
          for (Binding b : bound) {
            b.updateElement(event.getNewValue());
          }
        }
        fireHandlers(info.fullPath, event.getOldValue(), event.getNewValue());
      });
    }
  }

  public void unbind(String property) {
    List<Binding> removed = bindings.remove(property);
    if (removed != null) {
      for (Binding b : removed) {
        removeDomListener(b);
      }
    }
    propertyHandlers.remove(property);
  }

  public void unbindAll() {
    for (List<Binding> list : bindings.values()) {
      for (Binding b : list) {
        removeDomListener(b);
      }
    }
    bindings.clear();

    for (List<NestedBindingInfo> infos : nestedBindingsMap.values()) {
      for (NestedBindingInfo info : infos) {
        if (info.nestedHandle != null) {
          info.nestedHandle.unsubscribe();
          info.nestedHandle = null;
        }
      }
    }
    nestedBindingsMap.clear();

    listBindings.clear();

    globalHandlers.clear();
    propertyHandlers.clear();
  }

  public Set<String> getBoundProperties() {
    return bindings.keySet();
  }

  public void updateAllElements() {
    for (Map.Entry<String, List<Binding>> entry : bindings.entrySet()) {
      String property = entry.getKey();
      Object value = resolvePropertyValue(property);
      for (Binding b : entry.getValue()) {
        b.updateElement(value);
      }
    }

    for (List<NestedBindingInfo> infos : nestedBindingsMap.values()) {
      for (NestedBindingInfo info : infos) {
        if (info.nestedHandle != null) {
          info.nestedHandle.unsubscribe();
        }
        registerNestedListener(info);
      }
    }

    for (String property : listBindings.keySet()) {
      updateListComponent(property);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void onPropertyChanged(Object source, String property, Object oldValue, Object newValue) {
    List<Binding> propertyBindings = bindings.get(property);
    if (propertyBindings != null && !suppressEvents) {
      for (Binding b : propertyBindings) {
        b.updateElement(newValue);
      }
    }

    updateListComponent(property);

    fireHandlers(property, oldValue, newValue);

    List<NestedBindingInfo> nestedInfos = nestedBindingsMap.get(property);
    if (nestedInfos != null) {
      for (NestedBindingInfo info : nestedInfos) {
        if (info.nestedHandle != null) {
          info.nestedHandle.unsubscribe();
          info.nestedHandle = null;
        }
        registerNestedListener(info);
        Object current = resolvePropertyValue(info.fullPath);
        List<Binding> bound = bindings.get(info.fullPath);
        if (bound != null) {
          for (Binding b : bound) {
            b.updateElement(current);
          }
        }
      }
    }
  }

  public PropertyChangeUnsubscribeHandle addPropertyChangeHandler(
      PropertyChangeHandler<?> handler) {
    HandlerRegistration reg = new HandlerRegistration(handler);
    globalHandlers.add(reg);
    return () -> globalHandlers.remove(reg);
  }

  public PropertyChangeUnsubscribeHandle addPropertyChangeHandler(String property,
      PropertyChangeHandler<?> handler) {
    HandlerRegistration reg = new HandlerRegistration(handler);
    propertyHandlers.computeIfAbsent(property, k -> new ArrayList<>()).add(reg);
    return () -> {
      List<HandlerRegistration> list = propertyHandlers.get(property);
      if (list != null) {
        list.remove(reg);
        if (list.isEmpty()) {
          propertyHandlers.remove(property);
        }
      }
    };
  }

  public void onCollectionChanged(String propertyName) {
    updateListComponent(propertyName);
    Object currentValue = proxy.getPropertyValue(propertyName);
    onPropertyChanged(proxy, propertyName, currentValue, currentValue);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void updateListComponent(String propertyName) {
    ListComponent component = listBindings.get(propertyName);
    if (component != null) {
      Object value = proxy.getPropertyValue(propertyName);
      if (value instanceof List) {
        component.setItems((List) value);
      }
    }
  }

  private Object resolvePropertyValue(String property) {
    int dot = property.indexOf('.');
    if (dot < 0) {
      return proxy.getPropertyValue(property);
    }
    String head = property.substring(0, dot);
    String tail = property.substring(dot + 1);
    Object nested = proxy.getPropertyValue(head);
    if (nested instanceof BindableProxy) {
      BindableProxy<?> nestedProxy = (BindableProxy<?>) nested;
      int nextDot = tail.indexOf('.');
      if (nextDot < 0) {
        return nestedProxy.getPropertyValue(tail);
      }
      return nestedProxy.getBindingAgent().resolvePropertyValue(tail);
    }
    return null;
  }

  private void setModelProperty(String property, Object value) {
    int dot = property.indexOf('.');
    if (dot >= 0) {
      String head = property.substring(0, dot);
      String tail = property.substring(dot + 1);
      Object nested = proxy.getPropertyValue(head);
      if (nested instanceof BindableProxy) {
        ((BindableProxy<?>) nested).setPropertyValue(tail, value);
      }
      return;
    }
    propertySetter.accept(property, value);
  }

  private void fireHandlers(String property, Object oldValue, Object newValue) {
    PropertyChangeEvent event = new PropertyChangeEvent(proxy, property, oldValue, newValue);

    for (HandlerRegistration reg : globalHandlers) {
      reg.handler.onPropertyChange(event);
    }

    List<HandlerRegistration> specific = propertyHandlers.get(property);
    if (specific != null) {
      for (HandlerRegistration reg : specific) {
        reg.handler.onPropertyChange(event);
      }
    }
  }

  private void removeDomListener(Binding binding) {
    EventListener listener = binding.getDomListener();
    if (listener != null) {
      String eventType = binding.getAccessor().getChangeEventType();
      if (eventType != null) {
        binding.getElement().removeEventListener(eventType, listener);
      }
    }
  }

  private static class HandlerRegistration {
    final PropertyChangeHandler<?> handler;

    HandlerRegistration(PropertyChangeHandler<?> handler) {
      this.handler = handler;
    }
  }

  private static class NestedBindingInfo {
    final String fullPath;
    final String head;
    final String tail;
    PropertyChangeUnsubscribeHandle nestedHandle;

    NestedBindingInfo(String fullPath, String head, String tail) {
      this.fullPath = fullPath;
      this.head = head;
      this.tail = tail;
    }
  }
}
