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
import org.patternfly.component.drawer.Drawer;

import static org.jboss.elemento.EventType.click;
import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.drawer.Drawer.drawer;
import static org.patternfly.component.drawer.DrawerCloseButton.drawerCloseButton;
import static org.patternfly.component.drawer.DrawerContent.drawerContent;
import static org.patternfly.component.drawer.DrawerContentBody.drawerContentBody;
import static org.patternfly.component.drawer.DrawerPanel.drawerPanel;
import static org.patternfly.component.drawer.DrawerPanelBody.drawerPanelBody;
import static org.patternfly.component.drawer.DrawerPanelHead.drawerPanelHead;

@Page(path = "DrawerDemo")
@Singleton
@Templated("DrawerDemo.html")
public class DrawerDemo implements IsElement<HTMLDivElement> {

  @DataField
  Button toggleBtn = button().primary().text("Toggle drawer");

  @DataField
  Drawer basicDrawer = drawer()
      .addContent(drawerContent()
          .addBody(drawerContentBody()
              .text("Main content area. Click the button above to toggle the drawer panel.")))
      .addPanel(drawerPanel()
          .addHead(drawerPanelHead()
              .add(drawerPanelBody().text("Drawer panel content"))
              .addCloseButton(drawerCloseButton())));

  @PostConstruct
  void init() {
    toggleBtn.on(click, e -> basicDrawer.toggle());
  }
}
