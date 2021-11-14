/*
 * Copyright © 2021 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License")), you may not use this file except
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

public enum EventConstants {

  ONBLUR(4096), ONCHANGE(1024), ONCLICK(1), ONDBLCLICK(2), ONERROR(65536), ONFOCUS(
      2048), ONGESTURECHANGE(33554432), ONGESTUREEND(67108864), ONGESTURESTART(16777216), ONKEYDOWN(
          128), ONKEYPRESS(256), ONKEYUP(512), ONLOAD(32768), ONLOSECAPTURE(8192), ONMOUSEDOWN(
              4), ONMOUSEMOVE(64), ONMOUSEOUT(32), ONMOUSEOVER(16), ONMOUSEUP(8), ONMOUSEWHEEL(
                  131072), ONPASTE(524288), ONSCROLL(16384), ONTOUCHCANCEL(8388608), ONTOUCHEND(
                      4194304), ONTOUCHMOVE(2097152), ONTOUCHSTART(1048576), ONCONTEXTMENU(
                          262144), FOCUSEVENTS(6144), KEYEVENTS(896), MOUSEEVENTS(
                              124), TOUCHEVENTS(15728640), GESTUREEVENTS(117440512);

  public int code;

  EventConstants(int code) {
    this.code = code;
  }
}
