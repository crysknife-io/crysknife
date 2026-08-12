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
import org.patternfly.component.jumplinks.JumpLinks;

import static org.patternfly.component.jumplinks.JumpLinks.jumpLinks;
import static org.patternfly.component.jumplinks.JumpLinksItem.jumpLinksItem;

@Page(path = "JumpLinksDemo")
@Singleton
@Templated("JumpLinksDemo.html")
public class JumpLinksDemo implements IsElement<HTMLDivElement> {

  @DataField
  JumpLinks basicJumpLinks = jumpLinks("Jump to section")
      .add(jumpLinksItem("section-1", "Section 1", "#"))
      .add(jumpLinksItem("section-2", "Section 2", "#"))
      .add(jumpLinksItem("section-3", "Section 3", "#"))
      .add(jumpLinksItem("section-4", "Section 4", "#"));

  @DataField
  JumpLinks verticalJumpLinks = jumpLinks("Jump to section")
      .vertical()
      .add(jumpLinksItem("v-section-1", "Introduction", "#"))
      .add(jumpLinksItem("v-section-2", "Getting started", "#"))
      .add(jumpLinksItem("v-section-3", "Configuration", "#"))
      .add(jumpLinksItem("v-section-4", "API reference", "#"))
      .add(jumpLinksItem("v-section-5", "FAQ", "#"));
}
