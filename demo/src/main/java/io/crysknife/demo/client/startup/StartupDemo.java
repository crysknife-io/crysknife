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
package io.crysknife.demo.client.startup;

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
@Templated("startupdemo.html")
public class StartupDemo implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLDivElement resultOutput;

  @Inject
  @DataField
  HTMLButtonElement showBtn;

  @Inject
  StartupLog log;

  @EventHandler("showBtn")
  private void onShow(@ForEvent("click") MouseEvent event) {
    if (log.getEntries().isEmpty()) {
      resultOutput.textContent = "No startup beans were recorded.";
      return;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("Beans initialized at application startup:\n\n");
    for (int i = 0; i < log.getEntries().size(); i++) {
      sb.append(i + 1).append(". ").append(log.getEntries().get(i)).append("\n");
    }
    sb.append("\nThese ran BEFORE you navigated to this page!");
    resultOutput.textContent = sb.toString();
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
