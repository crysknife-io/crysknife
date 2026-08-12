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
import org.patternfly.component.toolbar.Toolbar;

import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.toolbar.Toolbar.toolbar;
import static org.patternfly.component.toolbar.ToolbarContent.toolbarContent;
import static org.patternfly.component.toolbar.ToolbarGroup.toolbarGroup;
import static org.patternfly.component.toolbar.ToolbarItem.toolbarItem;

@Page(path = "ToolbarDemo")
@Singleton
@Templated("ToolbarDemo.html")
public class ToolbarDemo implements IsElement<HTMLDivElement> {

  @DataField
  Toolbar basicToolbar = toolbar()
      .addContent(toolbarContent()
          .addGroup(toolbarGroup()
              .addItem(toolbarItem()
                  .add(button().secondary().text("Action")))
              .addItem(toolbarItem()
                  .add(button().secondary().text("Filter")))
              .addItem(toolbarItem()
                  .add(button().primary().text("Create")))));
}
