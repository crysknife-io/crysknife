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
import org.patternfly.component.tree.TreeView;

import static org.patternfly.component.tree.TreeView.treeView;
import static org.patternfly.component.tree.TreeViewItem.treeViewItem;

@Page(path = "TreeViewDemo")
@Singleton
@Templated("TreeViewDemo.html")
public class TreeViewDemo implements IsElement<HTMLDivElement> {

  @DataField
  TreeView basicTree = treeView().ariaLabel("Basic tree view")
      .add(treeViewItem("app-launcher", "Application launcher")
          .add(treeViewItem("al-1", "Application 1")
              .add(treeViewItem("al-1-1", "Settings")))
          .add(treeViewItem("al-2", "Application 2")))
      .add(treeViewItem("cost-mgmt", "Cost management")
          .add(treeViewItem("cm-1", "Application 3")
              .add(treeViewItem("cm-1-1", "Settings"))
              .add(treeViewItem("cm-1-2", "Current"))))
      .add(treeViewItem("sources", "Sources")
          .add(treeViewItem("src-1", "Application 4")
              .add(treeViewItem("src-1-1", "Settings")))
          .add(treeViewItem("src-2", "Application 5")));

  @DataField
  TreeView guidesTree = treeView().ariaLabel("Tree view with guides").guides()
      .add(treeViewItem("g-app-launcher", "Application launcher")
          .add(treeViewItem("g-al-1", "Application 1")
              .add(treeViewItem("g-al-1-1", "Settings"))
              .add(treeViewItem("g-al-1-2", "Loader")))
          .add(treeViewItem("g-al-2", "Application 2")
              .add(treeViewItem("g-al-2-1", "Settings"))))
      .add(treeViewItem("g-cost-mgmt", "Cost management")
          .add(treeViewItem("g-cm-1", "Application 3")))
      .add(treeViewItem("g-sources", "Sources")
          .add(treeViewItem("g-src-1", "Application 4"))
          .add(treeViewItem("g-src-2", "Application 5")));
}
