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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page
@Templated("interceptordemo.html")
public class InterceptorDemo implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLInputElement nameInput;

  @Inject
  @DataField
  HTMLDivElement resultOutput;

  @Inject
  @DataField
  HTMLTextAreaElement logOutput;

  @Inject
  @DataField
  HTMLButtonElement greetBtn;

  @Inject
  @DataField
  HTMLButtonElement addBtn;

  @Inject
  @DataField
  HTMLButtonElement noInterceptBtn;

  @Inject
  @DataField
  HTMLButtonElement clearBtn;

  @Inject
  GreetingService greetingService;

  @Inject
  LoggingInterceptor loggingInterceptor;

  @EventHandler("greetBtn")
  private void onGreet(@ForEvent("click") MouseEvent event) {
    String name = nameInput.value;
    if (name == null || name.isEmpty()) {
      name = "World";
    }
    String result = greetingService.greet(name);
    resultOutput.textContent = "Result: " + result;
    refreshLog();
  }

  @EventHandler("addBtn")
  private void onAdd(@ForEvent("click") MouseEvent event) {
    int result = greetingService.add(3, 7);
    resultOutput.textContent = "Result: 3 + 7 = " + result;
    refreshLog();
  }

  @EventHandler("noInterceptBtn")
  private void onNoIntercept(@ForEvent("click") MouseEvent event) {
    String result = greetingService.notIntercepted();
    resultOutput.textContent = "Result: " + result;
    refreshLog();
  }

  @EventHandler("clearBtn")
  private void onClear(@ForEvent("click") MouseEvent event) {
    loggingInterceptor.clearLog();
    resultOutput.textContent = "";
    logOutput.value = "";
  }

  private void refreshLog() {
    StringBuilder sb = new StringBuilder();
    for (String entry : loggingInterceptor.getLog()) {
      sb.append(entry).append("\n");
    }
    logOutput.value = sb.toString();
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
