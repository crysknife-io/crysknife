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
package io.crysknife.demo.client.provider;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.client.ManagedInstance;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page
@Templated("providerdemo.html")
public class ProviderDemo implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLDivElement resultOutput;

  @Inject
  @DataField
  HTMLButtonElement getBtn;

  @Inject
  @DataField
  HTMLButtonElement clearBtn;

  @Inject
  ManagedInstance<ExpensiveService> provider;

  private final List<Integer> instanceIds = new ArrayList<>();

  @EventHandler("getBtn")
  private void onGet(@ForEvent("click") MouseEvent event) {
    ExpensiveService instance = provider.get();
    instanceIds.add(instance.getInstanceId());
    refreshOutput();
  }

  @EventHandler("clearBtn")
  private void onClear(@ForEvent("click") MouseEvent event) {
    instanceIds.clear();
    resultOutput.textContent = "Click \"provider.get()\" to create instances";
  }

  private void refreshOutput() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < instanceIds.size(); i++) {
      sb.append("Instance #").append(i + 1).append(": instanceId=")
          .append(instanceIds.get(i)).append("\n");
    }
    resultOutput.textContent = sb.toString();
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
