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
import java.util.function.BiFunction;

import elemental2.core.Function;
import elemental2.core.Reflect;
import jsinterop.annotations.JsFunction;

public final class MethodInterceptorWrapper implements BiFunction<Object, String, Object> {

  @JsFunction
  public interface JsCallable {

    Object onInvoke(Object... args);
  }

  private final List<AroundInvokeCallback> chain;
  private JsCallable cachedWrapper;

  public MethodInterceptorWrapper(List<AroundInvokeCallback> chain) {
    this.chain = chain;
  }

  @Override
  public Object apply(Object target, String methodKey) {
    if (cachedWrapper == null) {
      Function originalFn = (Function) Reflect.get(target, methodKey);
      cachedWrapper = (Object... args) -> {
        AroundInvokeCallback terminalCall =
            ctx -> originalFn.apply(target, ctx.getParameters());
        InvocationContextImpl ctx =
            new InvocationContextImpl(target, args, chain, terminalCall);
        try {
          return ctx.proceed();
        } catch (RuntimeException e) {
          throw e;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      };
    }
    return cachedWrapper;
  }
}
