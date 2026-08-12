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
package io.crysknife.demo.client.decorator;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page
@Templated("decoratordemo.html")
public class DecoratorDemo implements IsElement<HTMLDivElement> {

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
  HTMLDivElement directOutput;

  @Inject
  @DataField
  HTMLButtonElement greetBtn;

  @Inject
  @DataField
  HTMLButtonElement directBtn;

  @Inject
  Greeter greeter;

  @Inject
  SimpleGreeter simpleGreeter;

  @EventHandler("greetBtn")
  private void onGreet(@ForEvent("click") MouseEvent event) {
    String name = nameInput.value;
    if (name == null || name.isEmpty()) {
      name = "World";
    }
    String result = greeter.greet(name);
    resultOutput.textContent = "Decorated result: " + result;
  }

  @EventHandler("directBtn")
  private void onDirect(@ForEvent("click") MouseEvent event) {
    String name = nameInput.value;
    if (name == null || name.isEmpty()) {
      name = "World";
    }
    String result = simpleGreeter.greet(name);
    directOutput.textContent = "Direct result: " + result;
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
