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
import org.patternfly.component.expandable.ExpandableSection;

import static org.patternfly.component.expandable.ExpandableSection.expandableSection;
import static org.patternfly.component.expandable.ExpandableSectionContent.expandableSectionContent;
import static org.patternfly.component.expandable.ExpandableSectionToggle.expandableSectionToggle;

@Page(path = "ExpandableSectionDemo")
@Singleton
@Templated("ExpandableSectionDemo.html")
public class ExpandableSectionDemo implements IsElement<HTMLDivElement> {

  @DataField
  ExpandableSection basicSection = expandableSection()
      .addToggle(expandableSectionToggle("Show more"))
      .addContent(expandableSectionContent()
          .text("This content is visible only when the component is expanded."));

  @DataField
  ExpandableSection dynamicToggle = expandableSection()
      .addToggle(expandableSectionToggle("Show more", "Show less"))
      .addContent(expandableSectionContent()
          .text("This section has dynamic toggle text that changes between 'Show more' and 'Show less'."));

  @DataField
  ExpandableSection disclosureSection = expandableSection()
      .disclosure()
      .addToggle(expandableSectionToggle("Show more", "Show less"))
      .addContent(expandableSectionContent()
          .text("Disclosure variation has a different visual style with a bordered appearance."));

  @DataField
  ExpandableSection indentedSection = expandableSection()
      .indented()
      .addToggle(expandableSectionToggle("Show more", "Show less"))
      .addContent(expandableSectionContent()
          .text("Indented content is offset from the toggle for a clearer visual hierarchy."));
}
