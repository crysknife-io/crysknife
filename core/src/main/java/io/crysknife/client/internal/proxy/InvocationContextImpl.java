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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.interceptor.InvocationContext;

import io.crysknife.client.internal.GwtIncompatible;

public class InvocationContextImpl implements InvocationContext {

  private final Object target;
  private final List<AroundInvokeCallback> chain;
  private final AroundInvokeCallback terminalCall;
  private final Map<String, Object> contextData = new HashMap<>();
  private Object[] parameters;
  private int currentIndex;

  public InvocationContextImpl(Object target, Object[] parameters,
      List<AroundInvokeCallback> chain, AroundInvokeCallback terminalCall) {
    this.target = target;
    this.parameters = parameters;
    this.chain = chain;
    this.terminalCall = terminalCall;
  }

  @Override
  public Object getTarget() {
    return target;
  }

  @Override
  public Object getTimer() {
    return null;
  }

  @GwtIncompatible
  @Override
  public java.lang.reflect.Constructor<?> getConstructor() {
    return null;
  }

  @GwtIncompatible
  @Override
  public java.lang.reflect.Method getMethod() {
    return null;
  }

  @Override
  public Object[] getParameters() {
    return parameters;
  }

  @Override
  public void setParameters(Object[] params) {
    this.parameters = params;
  }

  @Override
  public Map<String, Object> getContextData() {
    return contextData;
  }

  @Override
  public Object proceed() throws Exception {
    if (currentIndex < chain.size()) {
      return chain.get(currentIndex++).invoke(this);
    }
    return terminalCall.invoke(this);
  }
}
