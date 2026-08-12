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

import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import io.crysknife.ui.databinding.api.Converter;

/**
 * Represents a single binding between a model property and an HTML element.
 *
 * @author Dmitrii Tikhomirov
 */
public class Binding {

  private final HTMLElement element;
  private final String property;
  private final ElementAccessor accessor;
  private final Converter<Object, Object> converter;
  private EventListener domListener;

  @SuppressWarnings("unchecked")
  public Binding(HTMLElement element, String property, ElementAccessor accessor,
      Converter<?, ?> converter) {
    this.element = element;
    this.property = property;
    this.accessor = accessor;
    this.converter = (Converter<Object, Object>) converter;
  }

  public HTMLElement getElement() {
    return element;
  }

  public String getProperty() {
    return property;
  }

  public ElementAccessor getAccessor() {
    return accessor;
  }

  public Converter<Object, Object> getConverter() {
    return converter;
  }

  public void setDomListener(EventListener listener) {
    this.domListener = listener;
  }

  public EventListener getDomListener() {
    return domListener;
  }

  public void updateElement(Object modelValue) {
    Object displayValue = converter != null ? converter.toWidgetValue(modelValue) : modelValue;
    accessor.setValue(element, displayValue);
  }

  public Object readElement() {
    Object raw = accessor.getValue(element);
    return converter != null ? converter.toModelValue(raw) : raw;
  }
}
