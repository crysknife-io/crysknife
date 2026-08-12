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
import org.patternfly.component.list.List;

import static org.jboss.elemento.Elements.ol;
import static org.patternfly.component.list.List.list;
import static org.patternfly.component.list.ListItem.listItem;

@Page(path = "ListDemo")
@Singleton
@Templated("ListDemo.html")
public class ListDemo implements IsElement<HTMLDivElement> {

  @DataField
  List basicList = list()
      .addItem(listItem("list-basic-0").text("First"))
      .addItem(listItem("list-basic-1").text("Second"))
      .addItem(listItem("list-basic-2").text("Third"));

  @DataField
  List inlineList = list().inline()
      .addItem(listItem("list-inline-0").text("First"))
      .addItem(listItem("list-inline-1").text("Second"))
      .addItem(listItem("list-inline-2").text("Third"));

  @DataField
  List orderedList = list(ol())
      .addItem(listItem("list-ordered-0").text("First"))
      .addItem(listItem("list-ordered-1").text("Second"))
      .addItem(listItem("list-ordered-2").text("Third"));

  @DataField
  List plainList = list().plain()
      .addItem(listItem("list-plain-0").text("First"))
      .addItem(listItem("list-plain-1").text("Second"))
      .addItem(listItem("list-plain-2").text("Third"));

  @DataField
  List borderedList = list().plain().bordered()
      .addItem(listItem("list-bordered-0").text("First"))
      .addItem(listItem("list-bordered-1").text("Second"))
      .addItem(listItem("list-bordered-2").text("Third"));
}
