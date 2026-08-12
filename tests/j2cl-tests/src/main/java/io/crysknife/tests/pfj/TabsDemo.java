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
import org.patternfly.component.tabs.Tabs;

import static org.patternfly.component.tabs.Tab.tab;
import static org.patternfly.component.tabs.TabContent.tabContent;
import static org.patternfly.component.tabs.TabContentBody.tabContentBody;
import static org.patternfly.component.tabs.Tabs.tabs;

@Page(path = "TabsDemo")
@Singleton
@Templated("TabsDemo.html")
public class TabsDemo implements IsElement<HTMLDivElement> {

  @DataField
  Tabs defaultTabs = tabs()
      .add(tab("tab-users", "Users")
          .addContent(tabContent()
              .addBody(tabContentBody().text("Users tab content. Manage user accounts and permissions."))))
      .add(tab("tab-containers", "Containers")
          .addContent(tabContent()
              .addBody(tabContentBody().text("Containers tab content. View and manage running containers."))))
      .add(tab("tab-database", "Database")
          .addContent(tabContent()
              .addBody(tabContentBody().text("Database tab content. Monitor database connections and queries."))));

  @DataField
  Tabs boxTabs = tabs().box(true)
      .add(tab("box-users", "Users")
          .addContent(tabContent()
              .addBody(tabContentBody().text("Users tab content in a box variant."))))
      .add(tab("box-containers", "Containers")
          .addContent(tabContent()
              .addBody(tabContentBody().text("Containers tab content in a box variant."))))
      .add(tab("box-database", "Database")
          .addContent(tabContent()
              .addBody(tabContentBody().text("Database tab content in a box variant."))));

  @DataField
  Tabs fillTabs = tabs().fill(true)
      .add(tab("fill-users", "Users")
          .addContent(tabContent()
              .addBody(tabContentBody().text("Users tab content in a filled variant."))))
      .add(tab("fill-containers", "Containers")
          .addContent(tabContent()
              .addBody(tabContentBody().text("Containers tab content in a filled variant."))))
      .add(tab("fill-database", "Database")
          .addContent(tabContent()
              .addBody(tabContentBody().text("Database tab content in a filled variant."))));
}
