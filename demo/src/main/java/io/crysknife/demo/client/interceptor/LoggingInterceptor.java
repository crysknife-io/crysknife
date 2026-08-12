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
package io.crysknife.demo.client.interceptor;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import elemental2.dom.DomGlobal;

@ApplicationScoped
@Interceptor
@Logged
public class LoggingInterceptor {

  private final List<String> log = new ArrayList<>();

  @AroundInvoke
  public Object intercept(InvocationContext ctx) throws Exception {
    String target = ctx.getTarget().getClass().getSimpleName();
    String entry = "[INTERCEPTOR] " + target + " — before invocation";
    DomGlobal.console.log(entry);
    log.add(entry);

    Object result = ctx.proceed();

    String exit = "[INTERCEPTOR] " + target + " — after invocation, result: " + result;
    DomGlobal.console.log(exit);
    log.add(exit);

    return result;
  }

  public List<String> getLog() {
    return log;
  }

  public void clearLog() {
    log.clear();
  }
}
