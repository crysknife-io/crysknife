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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class InterceptorInfo {

  private final TypeMirror interceptorType;
  private final ExecutableElement aroundInvokeMethod;
  private final int priority;

  public InterceptorInfo(TypeMirror interceptorType, ExecutableElement aroundInvokeMethod,
      int priority) {
    this.interceptorType = interceptorType;
    this.aroundInvokeMethod = aroundInvokeMethod;
    this.priority = priority;
  }

  public TypeMirror getInterceptorType() {
    return interceptorType;
  }

  public ExecutableElement getAroundInvokeMethod() {
    return aroundInvokeMethod;
  }

  public int getPriority() {
    return priority;
  }
}
