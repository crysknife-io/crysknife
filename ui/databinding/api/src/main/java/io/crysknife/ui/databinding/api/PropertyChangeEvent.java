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
package io.crysknife.ui.databinding.api;

/**
 * Event fired when a bound model property changes.
 *
 * @param <T> the property value type
 * @author Dmitrii Tikhomirov
 */
public class PropertyChangeEvent<T> {

  private final Object source;
  private final String propertyName;
  private final T oldValue;
  private final T newValue;

  public PropertyChangeEvent(Object source, String propertyName, T oldValue, T newValue) {
    this.source = source;
    this.propertyName = propertyName;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public Object getSource() {
    return source;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public T getOldValue() {
    return oldValue;
  }

  public T getNewValue() {
    return newValue;
  }
}
