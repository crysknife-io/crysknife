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

import java.util.Objects;

import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;


public class EventHandlerHolder {


  final String eventType;
  final HTMLElement element;
  final EventListener eventListener;

  public EventHandlerHolder(String eventType, HTMLElement element, EventListener eventListener) {
    this.eventType = eventType;
    this.element = element;
    this.eventListener = eventListener;
  }

  public EventHandlerHolder attach() {
    element.addEventListener(eventType, eventListener);
    return this;
  }

  public EventHandlerHolder remove() {
    element.removeEventListener(eventType, eventListener);
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventType, element, eventListener);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    EventHandlerHolder that = (EventHandlerHolder) o;
    return Objects.equals(eventType, that.eventType) && Objects.equals(element, that.element)
        && Objects.equals(eventListener, that.eventListener);
  }
}
