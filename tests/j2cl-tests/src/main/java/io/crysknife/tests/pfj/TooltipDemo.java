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
import org.jboss.elemento.By;
import org.patternfly.component.button.Button;
import org.patternfly.component.tooltip.Tooltip;

import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.tooltip.Tooltip.tooltip;
import static org.patternfly.popper.Placement.bottom;
import static org.patternfly.popper.Placement.left;
import static org.patternfly.popper.Placement.right;

@Page(path = "TooltipDemo")
@Singleton
@Templated("TooltipDemo.html")
public class TooltipDemo implements IsElement<HTMLDivElement> {

  @DataField
  Button basicButton = button("Hover me for a tooltip").primary().id("tooltip-basic-btn");

  @DataField
  Tooltip basicTooltip = tooltip(By.id("tooltip-basic-btn"),
      "This is a tooltip with useful information.");

  @DataField
  Button bottomButton = button("Bottom").secondary().id("tooltip-bottom-btn");

  @DataField
  Tooltip bottomTooltip = tooltip(By.id("tooltip-bottom-btn"), "Bottom tooltip").placement(bottom);

  @DataField
  Button leftButton = button("Left").secondary().id("tooltip-left-btn");

  @DataField
  Tooltip leftTooltip = tooltip(By.id("tooltip-left-btn"), "Left tooltip").placement(left);

  @DataField
  Button rightButton = button("Right").secondary().id("tooltip-right-btn");

  @DataField
  Tooltip rightTooltip = tooltip(By.id("tooltip-right-btn"), "Right tooltip").placement(right);
}
