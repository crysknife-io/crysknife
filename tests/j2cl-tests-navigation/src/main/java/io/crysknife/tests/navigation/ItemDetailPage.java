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
package io.crysknife.tests.navigation;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.client.internal.collections.Multimap;
import io.crysknife.ui.navigation.client.Navigation;
import io.crysknife.ui.navigation.client.TransitionTo;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.navigation.client.annotation.PageShown;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(path = "items/{id}/detail")
@Templated("ItemDetailPage.html")
public class ItemDetailPage implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLDivElement paramDisplay;

  @Inject
  @DataField
  HTMLButtonElement backHome;

  @Inject
  Navigation navigation;

  @Inject
  TransitionTo<HomePage> toHome;

  @Override
  public HTMLDivElement getElement() {
    return root;
  }

  @EventHandler("backHome")
  public void onBackHome(@ForEvent("click") MouseEvent e) {
    toHome.go();
  }

  @PageShown
  public void onPageShown() {
    Multimap<String, String> state = navigation.getCurrentState();
    StringBuilder sb = new StringBuilder();
    sb.append("Path parameters:\n");
    for (String key : state.keys()) {
      List<String> values = state.get(key);
      sb.append("  ").append(key).append(" = ").append(values).append("\n");
    }
    String stateStr = sb.toString();
    NavigationTestLogger.log("[ItemDetailPage] @PageShown — " + stateStr);
    paramDisplay.textContent = stateStr;
  }
}
