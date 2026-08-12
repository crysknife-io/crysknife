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
package io.crysknife.demo.client.produces;

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
@Templated("producesdemo.html")
public class ProducesDemo implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLDivElement resultOutput;

  @Inject
  @DataField
  HTMLButtonElement singletonBtn;

  @Inject
  @DataField
  HTMLButtonElement dependentBtn;

  @Inject
  RandomIdService idService1;

  @Inject
  RandomIdService idService2;

  @Inject
  AppConfig config1;

  @Inject
  AppConfig config2;

  @EventHandler("singletonBtn")
  private void onSingleton(@ForEvent("click") MouseEvent event) {
    resultOutput.textContent = "@Singleton produced — idService1.id: " + idService1.getId()
        + ", idService2.id: " + idService2.getId()
        + " → same instance: " + (idService1.getId() == idService2.getId());
  }

  @EventHandler("dependentBtn")
  private void onDependent(@ForEvent("click") MouseEvent event) {
    resultOutput.textContent = "@Dependent produced — config1.instanceId: "
        + config1.getInstanceId() + ", config2.instanceId: " + config2.getInstanceId()
        + " → same instance: " + (config1.getInstanceId() == config2.getInstanceId());
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
