/*
 * Copyright © 2026 Treblereel
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

package io.crysknife.tests.pfj.translation;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;

@Page(path = "TranslationDemo")
@Singleton
@Templated("TranslationDemo.html")
public class TranslationDemo implements IsElement<HTMLDivElement> {

  @Inject
  DemoMessages demoMessages;

  @Inject
  AdminMessages adminMessages;

  @DataField
  HTMLElement welcomeResult;

  @DataField
  HTMLElement saveResult;

  @DataField
  HTMLElement cancelResult;

  @DataField
  HTMLElement deleteResult;

  @DataField
  HTMLElement boldResult;

  @DataField
  HTMLElement greetingResult;

  @DataField
  HTMLElement dashboardResult;

  @DataField
  HTMLElement settingsResult;

  @DataField
  HTMLElement usersOnlineResult;

  @PostConstruct
  public void init() {
    welcomeResult.textContent = demoMessages.welcome();
    saveResult.textContent = demoMessages.save();
    cancelResult.textContent = demoMessages.cancel();
    deleteResult.textContent = demoMessages.deleteButton();
    boldResult.innerHTML = demoMessages.boldLabel();
    greetingResult.textContent = demoMessages.greeting("World", "5");

    dashboardResult.textContent = adminMessages.dashboard();
    settingsResult.textContent = adminMessages.settings();
    usersOnlineResult.textContent = adminMessages.usersOnline("42");
  }
}
