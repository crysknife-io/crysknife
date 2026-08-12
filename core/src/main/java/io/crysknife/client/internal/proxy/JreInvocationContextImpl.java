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
package io.crysknife.client.internal.proxy;

import java.util.List;

import io.crysknife.client.internal.GwtIncompatible;

@GwtIncompatible
public class JreInvocationContextImpl extends InvocationContextImpl {

  private final java.lang.reflect.Method method;

  public JreInvocationContextImpl(Object target, Object[] parameters,
      List<AroundInvokeCallback> chain, AroundInvokeCallback terminalCall, java.lang.reflect.Method method) {
    super(target, parameters, chain, terminalCall);
    this.method = method;
  }

  @Override
  public java.lang.reflect.Method getMethod() {
    return method;
  }
}
