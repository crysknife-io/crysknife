/*
 * Copyright © 2025 Treblereel
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
package io.crysknife.samples.single;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Templated("hellocomponent.html")
public class HelloComponent implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  private HTMLDivElement root;

  @Inject
  @DataField
  private HTMLInputElement nameInput;

  @Inject
  @DataField
  private HTMLButtonElement greetBtn;

  @Inject
  @DataField
  private HTMLDivElement greeting;

  @PostConstruct
  public void init() {
    nameInput.placeholder = "Enter your name";
    greeting.textContent = "Click the button to say hello!";
  }

  @EventHandler("greetBtn")
  private void onGreet(@ForEvent("click") MouseEvent event) {
    String name = nameInput.value;
    if (name == null || name.isEmpty()) {
      name = "World";
    }
    greeting.textContent = "Hello, " + name + "!";
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
