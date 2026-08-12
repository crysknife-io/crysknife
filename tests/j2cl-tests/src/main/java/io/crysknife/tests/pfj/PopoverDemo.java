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

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.patternfly.component.button.Button;
import org.patternfly.popper.Placement;

import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.popover.Popover.popover;

@Page(path = "PopoverDemo")
@Singleton
@Templated("PopoverDemo.html")
public class PopoverDemo implements IsElement<HTMLDivElement> {

  @DataField
  Button popoverTrigger = button().primary().text("Toggle popover");

  @PostConstruct
  void init() {
    popover(popoverTrigger.element())
        .addHeader("Popover header")
        .addBody("This is the popover body content. Popovers provide more information on specific product areas.")
        .addFooter("Popover footer")
        .placement(Placement.bottom)
        .appendToBody();
  }
}
