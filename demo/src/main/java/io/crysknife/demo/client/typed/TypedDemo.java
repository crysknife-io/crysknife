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
package io.crysknife.demo.client.typed;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page
@Templated("typeddemo.html")
public class TypedDemo implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLDivElement resultOutput;

  @Inject
  @DataField
  HTMLButtonElement dogBtn;

  @Inject
  @DataField
  HTMLButtonElement animalBtn;

  @Inject
  Dog dog;

  @Inject
  Animal animal;

  @EventHandler("dogBtn")
  private void onDog(@ForEvent("click") MouseEvent event) {
    resultOutput.textContent = "dog.sound(): " + dog.sound()
        + " — class: " + dog.getClass().getSimpleName();
  }

  @EventHandler("animalBtn")
  private void onAnimal(@ForEvent("click") MouseEvent event) {
    resultOutput.textContent = "animal.sound(): " + animal.sound()
        + " — class: " + animal.getClass().getSimpleName()
        + " (Dog is not visible as Animal due to @Typed)";
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
