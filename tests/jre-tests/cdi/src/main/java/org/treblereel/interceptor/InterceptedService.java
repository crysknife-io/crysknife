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
package org.treblereel.interceptor;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InterceptedService {

  private boolean doWorkCalled;
  private String lastArg;

  @Logged
  public void doWork() {
    doWorkCalled = true;
  }

  @Logged
  public String greet(String name) {
    lastArg = name;
    return "Hello, " + name;
  }

  public void notIntercepted() {
    doWorkCalled = true;
  }

  public boolean isDoWorkCalled() {
    return doWorkCalled;
  }

  public String getLastArg() {
    return lastArg;
  }

  public void reset() {
    doWorkCalled = false;
    lastArg = null;
  }
}
