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

import java.util.Collections;
import java.util.List;

public class Element {

  private final String name;
  private final String mangledName;
  private final String element;

  private final boolean needCast;
  private final boolean elementoIsElement;
  private final boolean isElement;
  private final boolean useBean;
  private final List<String> roles;

  public Element(String name, String mangledName, String element) {
    this(name, mangledName, element, false, false, false, false);
  }

  public Element(String name, String mangledName, String element, boolean needCast) {
    this(name, mangledName, element, needCast, false, false, false);
  }

  public Element(String name, String mangledName, String element, boolean needCast,
      boolean elementoIsElement) {
    this(name, mangledName, element, needCast, elementoIsElement, false, false);
  }

  public Element(String name, String mangledName, String element, boolean needCast,
      boolean elementoIsElement, boolean isElement) {
    this(name, mangledName, element, needCast, elementoIsElement, isElement, false);
  }

  public Element(String name, String mangledName, String element, boolean needCast,
      boolean elementoIsElement, boolean isElement, boolean useBean) {
    this(name, mangledName, element, needCast, elementoIsElement, isElement, useBean,
        Collections.emptyList());
  }

  public Element(String name, String mangledName, String element, boolean needCast,
      boolean elementoIsElement, boolean isElement, boolean useBean, List<String> roles) {
    this.name = name;
    this.mangledName = mangledName;
    this.element = element;
    this.needCast = needCast;
    this.elementoIsElement = elementoIsElement;
    this.isElement = isElement;
    this.useBean = useBean;
    this.roles = roles != null ? roles : Collections.emptyList();
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

  public boolean isElementoIsElement() {
    return elementoIsElement;
  }

  public boolean isIsElement() {
    return isElement;
  }

  public boolean isUseBean() {
    return useBean;
  }

  public List<String> getRoles() {
    return roles;
  }
}
