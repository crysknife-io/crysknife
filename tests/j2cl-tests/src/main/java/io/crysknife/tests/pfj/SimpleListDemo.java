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
import org.patternfly.component.list.SimpleList;

import static org.patternfly.component.list.SimpleList.simpleList;
import static org.patternfly.component.list.SimpleListGroup.simpleListGroup;
import static org.patternfly.component.list.SimpleListItem.simpleListItem;

@Page(path = "SimpleListDemo")
@Singleton
@Templated("SimpleListDemo.html")
public class SimpleListDemo implements IsElement<HTMLDivElement> {

  @DataField
  SimpleList basicSimpleList = simpleList()
      .addItem(simpleListItem("sl-0", "List item 1"))
      .addItem(simpleListItem("sl-1", "List item 2"))
      .addItem(simpleListItem("sl-2", "List item 3"));

  @DataField
  SimpleList groupedSimpleList = simpleList()
      .addGroup(simpleListGroup("Group 1")
          .addItem(simpleListItem("sl-g1-0", "List item 1"))
          .addItem(simpleListItem("sl-g1-1", "List item 2"))
          .addItem(simpleListItem("sl-g1-2", "List item 3")))
      .addGroup(simpleListGroup("Group 2")
          .addItem(simpleListItem("sl-g2-0", "List item 1"))
          .addItem(simpleListItem("sl-g2-1", "List item 2")));
}
