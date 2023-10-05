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

package io.crysknife;


import io.crysknife.generator.context.IOCContext;

public class ContextHolder {

  private static volatile ContextHolder instance;

  private IOCContext context;

  private ContextHolder() {}

  public static ContextHolder getInstance() {
    if (instance == null) {
      synchronized (ContextHolder.class) {
        if (instance == null) {
          instance = new ContextHolder();
        }
      }
    }
    return instance;
  }

  IOCContext getContext() {
    return context;
  }

  void setContext(IOCContext context) {
    this.context = context;
  }

}
