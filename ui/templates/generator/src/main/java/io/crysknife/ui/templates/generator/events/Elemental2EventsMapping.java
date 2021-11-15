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

package io.crysknife.ui.templates.generator.events;

import com.google.common.collect.HashBiMap;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.CanPlayThroughEvent;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ContextMenuEvent;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.DragEndEvent;
import org.gwtproject.event.dom.client.DragEnterEvent;
import org.gwtproject.event.dom.client.DragEvent;
import org.gwtproject.event.dom.client.DragLeaveEvent;
import org.gwtproject.event.dom.client.DragOverEvent;
import org.gwtproject.event.dom.client.DragStartEvent;
import org.gwtproject.event.dom.client.DropEvent;
import org.gwtproject.event.dom.client.EndedEvent;
import org.gwtproject.event.dom.client.ErrorEvent;
import org.gwtproject.event.dom.client.FocusEvent;
import org.gwtproject.event.dom.client.GestureChangeEvent;
import org.gwtproject.event.dom.client.GestureEndEvent;
import org.gwtproject.event.dom.client.GestureStartEvent;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.LoadEvent;
import org.gwtproject.event.dom.client.LoadedMetadataEvent;
import org.gwtproject.event.dom.client.LoseCaptureEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseOverEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseWheelEvent;
import org.gwtproject.event.dom.client.ProgressEvent;
import org.gwtproject.event.dom.client.ScrollEvent;
import org.gwtproject.event.dom.client.TouchCancelEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;

public class Elemental2EventsMapping {

  static final HashBiMap<String, String> EVENTS = HashBiMap.create();

  static {
    EVENTS.put(BlurEvent.class.getCanonicalName(), "blur");
    EVENTS.put(CanPlayThroughEvent.class.getCanonicalName(), "canplaythrough");
    EVENTS.put(ChangeEvent.class.getCanonicalName(), "change");
    EVENTS.put(ClickEvent.class.getCanonicalName(), "click");
    EVENTS.put(ContextMenuEvent.class.getCanonicalName(), "contextmenu");
    EVENTS.put(DoubleClickEvent.class.getCanonicalName(), "dblclick");
    EVENTS.put(DragEndEvent.class.getCanonicalName(), "dragend");
    EVENTS.put(DragEnterEvent.class.getCanonicalName(), "dragenter");
    EVENTS.put(DragEvent.class.getCanonicalName(), "drag");
    EVENTS.put(DragLeaveEvent.class.getCanonicalName(), "dragleave");
    EVENTS.put(DragOverEvent.class.getCanonicalName(), "dragover");
    EVENTS.put(DragStartEvent.class.getCanonicalName(), "dragstart");
    EVENTS.put(DropEvent.class.getCanonicalName(), "drop");
    EVENTS.put(EndedEvent.class.getCanonicalName(), "ended");
    EVENTS.put(ErrorEvent.class.getCanonicalName(), "error");
    EVENTS.put(FocusEvent.class.getCanonicalName(), "focus");
    EVENTS.put(GestureChangeEvent.class.getCanonicalName(), "gesturechange");
    EVENTS.put(GestureEndEvent.class.getCanonicalName(), "gestureend");
    EVENTS.put(GestureStartEvent.class.getCanonicalName(), "gesturestart");
    EVENTS.put(KeyPressEvent.class.getCanonicalName(), "keypress");
    EVENTS.put(KeyDownEvent.class.getCanonicalName(), "keydown");
    EVENTS.put(KeyUpEvent.class.getCanonicalName(), "keyup");
    EVENTS.put(LoadedMetadataEvent.class.getCanonicalName(), "loadedmetadata");
    EVENTS.put(LoadEvent.class.getCanonicalName(), "load");
    EVENTS.put(LoseCaptureEvent.class.getCanonicalName(), "losecapture");
    EVENTS.put(MouseDownEvent.class.getCanonicalName(), "mousedown");
    EVENTS.put(MouseMoveEvent.class.getCanonicalName(), "mousemove");
    EVENTS.put(MouseOutEvent.class.getCanonicalName(), "mouseout");
    EVENTS.put(MouseOverEvent.class.getCanonicalName(), "mouseover");
    EVENTS.put(MouseUpEvent.class.getCanonicalName(), "mouseup");
    EVENTS.put(MouseWheelEvent.class.getCanonicalName(), "mousewheel");
    EVENTS.put(ProgressEvent.class.getCanonicalName(), "progress");
    EVENTS.put(ScrollEvent.class.getCanonicalName(), "scroll");
    EVENTS.put(TouchCancelEvent.class.getCanonicalName(), "touchcancel");
    EVENTS.put(TouchEndEvent.class.getCanonicalName(), "touchend");
    EVENTS.put(TouchMoveEvent.class.getCanonicalName(), "touchmove");
    EVENTS.put(TouchStartEvent.class.getCanonicalName(), "touchstart");
  }


}
