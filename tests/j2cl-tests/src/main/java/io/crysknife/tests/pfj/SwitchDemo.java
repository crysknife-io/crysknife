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
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.patternfly.component.switch_.Switch;

import static org.patternfly.component.switch_.Switch.switch_;

@Page(path = "SwitchDemo")
@Singleton
@Templated("SwitchDemo.html")
public class SwitchDemo implements IsElement<HTMLDivElement> {

  @DataField
  Switch basicSwitch = switch_("basic-switch", "basic-switch", true)
      .label("Togglable option");

  @DataField
  Switch reversedSwitch = switch_("reversed-switch", "reversed-switch", true)
      .reversed()
      .label("Reversed layout");

  @DataField
  Switch disabledSwitch = switch_("disabled-switch", "disabled-switch", false)
      .disabled()
      .label("Disabled switch");

  @DataField
  Switch noLabelSwitch = switch_("no-label-switch", "no-label-switch", true)
      .checkIcon()
      .ariaLabel("Switch without visible label");
}
