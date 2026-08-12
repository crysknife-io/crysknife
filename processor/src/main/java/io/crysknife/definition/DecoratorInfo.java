/*
 * Copyright © 2024 Treblereel
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
package io.crysknife.definition;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class DecoratorInfo {

  private final TypeMirror decoratorType;
  private final TypeMirror delegateType;
  private final VariableElement delegateField;
  private final int priority;
  private TypeMirror delegateResolvesTo;

  public DecoratorInfo(TypeMirror decoratorType, TypeMirror delegateType,
      VariableElement delegateField, int priority) {
    this.decoratorType = decoratorType;
    this.delegateType = delegateType;
    this.delegateField = delegateField;
    this.priority = priority;
  }

  public TypeMirror getDecoratorType() {
    return decoratorType;
  }

  public TypeMirror getDelegateType() {
    return delegateType;
  }

  public VariableElement getDelegateField() {
    return delegateField;
  }

  public int getPriority() {
    return priority;
  }

  public TypeMirror getDelegateResolvesTo() {
    return delegateResolvesTo;
  }

  public void setDelegateResolvesTo(TypeMirror delegateResolvesTo) {
    this.delegateResolvesTo = delegateResolvesTo;
  }
}
