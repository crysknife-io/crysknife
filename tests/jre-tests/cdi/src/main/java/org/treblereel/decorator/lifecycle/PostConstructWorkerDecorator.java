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
package org.treblereel.decorator.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Decorator
@Priority(1000)
public class PostConstructWorkerDecorator implements Worker {

  @Inject
  @Delegate
  Worker delegate;

  private boolean initialized;
  private String prefix;

  @PostConstruct
  public void init() {
    initialized = true;
    prefix = "PC:";
  }

  @Override
  public String work() {
    return prefix + delegate.work();
  }

  public boolean isInitialized() {
    return initialized;
  }
}
