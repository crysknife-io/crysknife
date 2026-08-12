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
import io.crysknife.ui.navigation.client.annotation.PageShowing;
import io.crysknife.ui.navigation.client.annotation.PageShown;
import io.crysknife.ui.navigation.client.annotation.PageState;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(path = "PageB")
@Templated("PageB.html")
public class PageB implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  HTMLDivElement stateDisplay;

  @Inject
  @DataField
  HTMLButtonElement backHome;

  @Inject
  TransitionTo<HomePage> toHome;

  @PageState
  String userId;

  @PageState
  String action;

  @PageState
  String key1;

  @PageState
  String key2;

  @PageState(value = "renamed_param")
  String renamedField;

  @PageState(defaultValue = "fallback")
  String withDefault;

  @Override
  public HTMLDivElement getElement() {
    return root;
  }

  @EventHandler("backHome")
  public void onBackHome(@ForEvent("click") MouseEvent e) {
    toHome.go();
  }

  @PageShowing
  public void onPageShowing() {
    NavigationTestLogger.log("[PageB] @PageShowing");
  }

  @PageShown
  public void onPageShown() {
    NavigationTestLogger.log("[PageB] @PageShown");
    StringBuilder sb = new StringBuilder();
    sb.append("userId=").append(userId);
    sb.append("|action=").append(action);
    sb.append("|key1=").append(key1);
    sb.append("|key2=").append(key2);
    sb.append("|renamedField=").append(renamedField);
    sb.append("|withDefault=").append(withDefault);
    String stateStr = sb.toString();
    NavigationTestLogger.log("[PageB] " + stateStr);
    stateDisplay.textContent = stateStr;
  }
}
