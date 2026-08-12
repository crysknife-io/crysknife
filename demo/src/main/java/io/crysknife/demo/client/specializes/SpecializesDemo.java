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
package io.crysknife.demo.client.specializes;

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
@Templated("specializesdemo.html")
public class SpecializesDemo implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLDivElement resultOutput;

  @Inject
  @DataField
  HTMLButtonElement nameBtn;

  @Inject
  @DataField
  HTMLButtonElement classBtn;

  @Inject
  BaseService service;

  @EventHandler("nameBtn")
  private void onName(@ForEvent("click") MouseEvent event) {
    resultOutput.textContent = "service.getName(): " + service.getName();
  }

  @EventHandler("classBtn")
  private void onClass(@ForEvent("click") MouseEvent event) {
    resultOutput.textContent = "service.getClass().getSimpleName(): "
        + service.getClass().getSimpleName();
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
