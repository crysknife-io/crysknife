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
import io.crysknife.ui.navigation.client.TransitionTo;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.navigation.client.annotation.PageHidden;
import io.crysknife.ui.navigation.client.annotation.PageHiding;
import io.crysknife.ui.navigation.client.annotation.PageShowing;
import io.crysknife.ui.navigation.client.annotation.PageShown;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(path = "PageA")
@Templated("PageA.html")
public class PageA implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  TransitionTo<HomePage> toHome;

  @Inject
  @DataField
  HTMLButtonElement backHome;

  @Override
  public HTMLDivElement getElement() {
    return root;
  }

  @EventHandler("backHome")
  public void onBackHome(@ForEvent("click") MouseEvent e) {
    NavigationTestLogger.log("[PageA] Navigating back to HomePage");
    toHome.go();
  }

  @PageShowing
  public void onPageShowing() {
    NavigationTestLogger.log("[PageA] @PageShowing");
  }

  @PageShown
  public void onPageShown() {
    NavigationTestLogger.log("[PageA] @PageShown");
  }

  @PageHiding
  public void onPageHiding() {
    NavigationTestLogger.log("[PageA] @PageHiding");
  }

  @PageHidden
  public void onPageHidden() {
    NavigationTestLogger.log("[PageA] @PageHidden");
  }
}
