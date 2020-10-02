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

package org.jboss.gwt.elemento.processor.context;

import java.util.Arrays;

import javax.lang.model.type.TypeMirror;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 6/2/19
 */
public class EventHandlerInfo {

  private DataElementInfo info;

  private String[] events;

  private String methodName;

  private String eventType;

  public EventHandlerInfo(DataElementInfo info, String[] events, String methodName,
      String eventType) {

    this.info = info;
    this.events = events;
    this.methodName = methodName;
    this.eventType = eventType;
  }

  public DataElementInfo getInfo() {
    return info;
  }

  public String[] getEvents() {
    return events;
  }

  @Override
  public String toString() {
    return "EventHandlerInfo{" + "info=" + info + ", events=" + Arrays.toString(events)
        + ", methodName=" + methodName + '}';
  }

  public String getMethodName() {
    return methodName;
  }

  public String getEventType() {
    return eventType;
  }
}
