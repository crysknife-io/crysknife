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
import org.patternfly.component.menu.Dropdown;

import static org.patternfly.component.menu.Dropdown.dropdown;
import static org.patternfly.component.menu.DropdownMenu.dropdownMenu;
import static org.patternfly.component.menu.MenuContent.menuContent;
import static org.patternfly.component.menu.MenuItem.menuItem;
import static org.patternfly.component.menu.MenuList.menuList;

@Page(path = "DropdownDemo")
@Singleton
@Templated("DropdownDemo.html")
public class DropdownDemo implements IsElement<HTMLDivElement> {

  @DataField
  Dropdown basicDropdown = dropdown("Dropdown")
      .addMenu(dropdownMenu()
          .addContent(menuContent()
              .addList(menuList()
                  .add(menuItem("action-1", "Action"))
                  .add(menuItem("link-1", "Link"))
                  .add(menuItem("disabled-1", "Disabled action"))
                  .add(menuItem("action-2", "Another action")))));

  @DataField
  Dropdown iconDropdown = dropdown(org.patternfly.icon.IconSets.fas.cog(), "Settings")
      .addMenu(dropdownMenu()
          .addContent(menuContent()
              .addList(menuList()
                  .add(menuItem("pref", "Preferences"))
                  .add(menuItem("profile", "Profile"))
                  .add(menuItem("logout", "Logout")))));
}
