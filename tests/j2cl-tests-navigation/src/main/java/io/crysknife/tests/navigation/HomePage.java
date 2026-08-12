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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.client.internal.collections.Multimap;
import io.crysknife.ui.navigation.client.TransitionTo;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.navigation.client.annotation.PageHidden;
import io.crysknife.ui.navigation.client.annotation.PageHiding;
import io.crysknife.ui.navigation.client.annotation.PageShowing;
import io.crysknife.ui.navigation.client.annotation.PageShown;
import io.crysknife.ui.navigation.client.DefaultPage;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(role = DefaultPage.class)
@Templated("HomePage.html")
public class HomePage implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  TransitionTo<PageA> toPageA;

  @Inject
  TransitionTo<PageB> toPageB;

  @Inject
  @DataField
  HTMLButtonElement goToA;

  @Inject
  @DataField
  HTMLButtonElement goToBWithState;

  @Override
  public HTMLDivElement getElement() {
    return root;
  }

  @EventHandler("goToA")
  public void onGoToA(@ForEvent("click") MouseEvent e) {
    NavigationTestLogger.log("[HomePage] Navigating to PageA via TransitionTo");
    toPageA.go();
  }

  @EventHandler("goToBWithState")
  public void onGoToBWithState(@ForEvent("click") MouseEvent e) {
    Multimap<String, String> state = new Multimap<>();
    state.put("userId", "123");
    state.put("action", "edit");
    NavigationTestLogger.log("[HomePage] Navigating to PageB with state: userId=123, action=edit");
    toPageB.go(state);
  }

  @PageShowing
  public void onPageShowing() {
    NavigationTestLogger.log("[HomePage] @PageShowing");
  }

  @PageShown
  public void onPageShown() {
    NavigationTestLogger.log("[HomePage] @PageShown");
  }

  @PageHiding
  public void onPageHiding() {
    NavigationTestLogger.log("[HomePage] @PageHiding");
  }

  @PageHidden
  public void onPageHidden() {
    NavigationTestLogger.log("[HomePage] @PageHidden");
  }
}
