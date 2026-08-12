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
import org.patternfly.component.list.ActionList;

import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.list.ActionList.actionList;
import static org.patternfly.component.list.ActionListGroup.actionListGroup;
import static org.patternfly.component.list.ActionListItem.actionListItem;

@Page(path = "ActionListDemo")
@Singleton
@Templated("ActionListDemo.html")
public class ActionListDemo implements IsElement<HTMLDivElement> {

  @DataField
  ActionList singleGroup = actionList()
      .addItem(actionListGroup()
          .addItem(actionListItem().add(button("Next").primary()))
          .addItem(actionListItem().add(button("Back").secondary())));

  @DataField
  ActionList multipleGroups = actionList()
      .addItem(actionListGroup()
          .addItem(actionListItem().add(button("Next").primary()))
          .addItem(actionListItem().add(button("Back").secondary())))
      .addItem(actionListGroup()
          .addItem(actionListItem().add(button("Submit").primary()))
          .addItem(actionListItem().add(button("Cancel").link())));

  @DataField
  ActionList cancelButton = actionList()
      .addItem(actionListGroup()
          .addItem(actionListItem().add(button("Save").primary()))
          .addItem(actionListItem().add(button("Cancel").link())));
}
