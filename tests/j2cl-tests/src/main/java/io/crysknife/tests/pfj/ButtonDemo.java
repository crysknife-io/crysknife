/*
 * Copyright © 2023 Treblereel
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

package io.crysknife.tests.pfj;

import jakarta.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.DefaultPage;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.patternfly.component.button.Button;

import static org.patternfly.component.button.Button.button;

@Page(role = DefaultPage.class)
@Singleton
@Templated("ButtonDemo.html")
public class ButtonDemo implements IsElement<HTMLDivElement> {

  @DataField
  Button primaryBtn = button("Primary").primary();

  @DataField
  Button secondaryBtn = button("Secondary").secondary();

  @DataField
  Button tertiaryBtn = button("Tertiary").tertiary();

  @DataField
  Button dangerBtn = button("Danger").danger();

  @DataField
  Button warningBtn = button("Warning").warning();

  @DataField
  Button linkBtn = button("Link").link();

  @DataField
  Button disabledBtn = button("Disabled").primary().disabled();

  @EventHandler("primaryBtn")
  public void onPrimaryClick(@ForEvent("click") final MouseEvent e) {
    primaryBtn.text("Clicked!");
  }

  @EventHandler
  public void onRootClick(@ForEvent("click") final MouseEvent e) {
  }
}
