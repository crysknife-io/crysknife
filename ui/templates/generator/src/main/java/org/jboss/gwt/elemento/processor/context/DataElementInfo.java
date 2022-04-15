/*
 * Copyright (C) 2014 Google, Inc.
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

import com.google.common.base.Strings;
import elemental2.dom.HTMLElement;

import javax.lang.model.type.TypeMirror;

public class DataElementInfo {

  public enum Kind {
    // Name them after the related type for nicer error messages
    HTMLElement, IsElement, IsWidget, Custom, GWT_DOM
  }

  private final TypeMirror type;
  private final String name;
  private final String selector;
  private final Kind kind;
  private final boolean returnedByMethod;

  public DataElementInfo(final TypeMirror type, final String name, final String selector,
      final Kind kind, boolean returnedByMethod) {
    this.type = type;
    this.name = name;
    this.selector = Strings.emptyToNull(selector) == null ? name : selector;
    this.kind = kind;
    this.returnedByMethod = returnedByMethod;
  }

  @Override
  public String toString() {
    return "@DataElement " + type + " " + name + " " + selector + " (" + kind
        + (returnedByMethod ? ", return by method" : "") + ")";
  }

  public TypeMirror getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getSelector() {
    return selector;
  }

  public Kind getKind() {
    return kind;
  }

  public boolean isReturnedByMethod() {
    return returnedByMethod;
  }

  public String getFieldOrMethod() {
    return returnedByMethod ? name + "()" : name;
  }

  public boolean needsCast() {
    return kind == Kind.HTMLElement && !type.equals(HTMLElement.class.getName());
  }
}
