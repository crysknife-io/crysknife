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

public class Element {

  private final String name;
  private final String mangledName;
  private final String element;

  private final boolean needCast;

  public Element(String name, String mangledName, String element) {
    this(name, mangledName, element, false);
  }

  public Element(String name, String mangledName, String element, boolean needCast) {
    this.name = name;
    this.mangledName = mangledName;
    this.element = element;
    this.needCast = needCast;
  }

  public String getName() {
    return name;
  }

  public String getMangledName() {
    return mangledName;
  }

  public String getElement() {
    return element;
  }

  public boolean isNeedCast() {
    return needCast;
  }
}
