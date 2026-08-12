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
package io.crysknife.demo.client.managedinstance;

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
@Templated("managedinstancedemo.html")
public class ManagedInstanceDemo implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLDivElement resultOutput;

  @Inject
  @DataField
  HTMLButtonElement createBtn;

  @Inject
  @DataField
  HTMLButtonElement destroyAllBtn;

  @Inject
  ManagedInstance<Worker> workers;

  private final List<Worker> createdWorkers = new ArrayList<>();

  @EventHandler("createBtn")
  private void onCreate(@ForEvent("click") MouseEvent event) {
    Worker worker = workers.get();
    createdWorkers.add(worker);
    refreshOutput();
  }

  @EventHandler("destroyAllBtn")
  private void onDestroyAll(@ForEvent("click") MouseEvent event) {
    workers.destroyAll();
    refreshOutput();
  }

  private void refreshOutput() {
    if (createdWorkers.isEmpty()) {
      resultOutput.textContent = "Click \"Create worker\" to create instances";
      return;
    }
    StringBuilder sb = new StringBuilder();
    for (Worker w : createdWorkers) {
      sb.append("Worker #").append(w.getId())
          .append(w.isDestroyed() ? " [DESTROYED — @PreDestroy called]" : " [active]")
          .append("\n");
    }
    resultOutput.textContent = sb.toString();
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
