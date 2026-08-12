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
import org.patternfly.component.breadcrumb.Breadcrumb;

import static org.patternfly.component.breadcrumb.Breadcrumb.breadcrumb;
import static org.patternfly.component.breadcrumb.BreadcrumbItem.breadcrumbItem;

@Page(path = "BreadcrumbDemo")
@Singleton
@Templated("BreadcrumbDemo.html")
public class BreadcrumbDemo implements IsElement<HTMLDivElement> {

  @DataField
  Breadcrumb basicBreadcrumb = breadcrumb()
      .addItem(breadcrumbItem("bc-0", "Section home", "#"))
      .addItem(breadcrumbItem("bc-1", "Section title", "#"))
      .addItem(breadcrumbItem("bc-2", "Section title", "#"))
      .addItem(breadcrumbItem("bc-3", "Section landing", "#").active());

  @DataField
  Breadcrumb noHomeLinkBreadcrumb = breadcrumb()
      .addItem(breadcrumbItem("bc-nhl-0", "Section home"))
      .addItem(breadcrumbItem("bc-nhl-1", "Section title", "#"))
      .addItem(breadcrumbItem("bc-nhl-2", "Section title", "#"))
      .addItem(breadcrumbItem("bc-nhl-3", "Section landing", "#").active());

  @DataField
  Breadcrumb buttonsBreadcrumb = breadcrumb()
      .addItem(breadcrumbItem("bc-btn-0", "Section home"))
      .addItem(breadcrumbItem("bc-btn-1", "Section title"))
      .addItem(breadcrumbItem("bc-btn-2", "Section title"))
      .addItem(breadcrumbItem("bc-btn-3", "Section landing").active());
}
