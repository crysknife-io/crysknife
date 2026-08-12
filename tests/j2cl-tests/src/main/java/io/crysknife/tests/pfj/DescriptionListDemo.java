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
import org.patternfly.component.list.DescriptionList;

import static org.patternfly.component.list.DescriptionList.descriptionList;
import static org.patternfly.component.list.DescriptionListDescription.descriptionListDescription;
import static org.patternfly.component.list.DescriptionListGroup.descriptionListGroup;
import static org.patternfly.component.list.DescriptionListTerm.descriptionListTerm;

@Page(path = "DescriptionListDemo")
@Singleton
@Templated("DescriptionListDemo.html")
public class DescriptionListDemo implements IsElement<HTMLDivElement> {

  @DataField
  DescriptionList basicList = descriptionList()
      .addItem(descriptionListGroup("dl-basic-name")
          .addTerm(descriptionListTerm("Name"))
          .addDescription(descriptionListDescription("Crysknife Framework")))
      .addItem(descriptionListGroup("dl-basic-version")
          .addTerm(descriptionListTerm("Version"))
          .addDescription(descriptionListDescription("0.10")))
      .addItem(descriptionListGroup("dl-basic-desc")
          .addTerm(descriptionListTerm("Description"))
          .addDescription(descriptionListDescription("Jakarta CDI-like dependency injection for J2CL")))
      .addItem(descriptionListGroup("dl-basic-license")
          .addTerm(descriptionListTerm("License"))
          .addDescription(descriptionListDescription("Apache License 2.0")));

  @DataField
  DescriptionList horizontalList = descriptionList().horizontal()
      .addItem(descriptionListGroup("dl-hz-name")
          .addTerm(descriptionListTerm("Name"))
          .addDescription(descriptionListDescription("PatternFly Java")))
      .addItem(descriptionListGroup("dl-hz-ns")
          .addTerm(descriptionListTerm("Namespace"))
          .addDescription(descriptionListDescription("org.patternfly")))
      .addItem(descriptionListGroup("dl-hz-type")
          .addTerm(descriptionListTerm("Type"))
          .addDescription(descriptionListDescription("UI Component Library")));

  @DataField
  DescriptionList compactList = descriptionList().compact()
      .addItem(descriptionListGroup("dl-cp-os")
          .addTerm(descriptionListTerm("OS"))
          .addDescription(descriptionListDescription("Linux")))
      .addItem(descriptionListGroup("dl-cp-cpu")
          .addTerm(descriptionListTerm("CPU"))
          .addDescription(descriptionListDescription("4 cores")))
      .addItem(descriptionListGroup("dl-cp-mem")
          .addTerm(descriptionListTerm("Memory"))
          .addDescription(descriptionListDescription("16 GiB")));
}
