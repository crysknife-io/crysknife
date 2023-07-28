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

package io.crysknife.ui.templates.generator.dto;

public class Event {

  private final String[] eventTypes;
  private final String mangleName;
  private final String clazz;
  private final String call;

  public Event(String[] eventTypes, String mappedName, String clazz, String call) {
    this.eventTypes = eventTypes;
    this.mangleName = mappedName;
    this.clazz = clazz;
    this.call = call;
  }

  public String getMangleName() {
    return mangleName;
  }

  public String getClazz() {
    return clazz;
  }

  public String getCall() {
    return call;
  }

  public String[] getEventTypes() {
    return eventTypes;
  }
}
