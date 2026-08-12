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
import org.patternfly.component.list.DataList;

import static org.jboss.elemento.Elements.span;
import static org.patternfly.component.list.DataList.dataList;
import static org.patternfly.component.list.DataListCell.dataListCell;
import static org.patternfly.component.list.DataListItem.dataListItem;

@Page(path = "DataListDemo")
@Singleton
@Templated("DataListDemo.html")
public class DataListDemo implements IsElement<HTMLDivElement> {

  @DataField
  DataList basicDataList = dataList()
      .addItem(dataListItem("dl-basic-0")
          .addCell(dataListCell()
              .add(span().id("dl-basic-0").text("Primary content")))
          .addCell(dataListCell().text("Secondary content")))
      .addItem(dataListItem("dl-basic-1")
          .addCell(dataListCell()
              .add(span().id("dl-basic-1").text("Second row primary")))
          .addCell(dataListCell().text("Second row secondary")));

  @DataField
  DataList compactDataList = dataList().compact()
      .addItem(dataListItem("dl-compact-0")
          .addCell(dataListCell()
              .add(span().id("dl-compact-0").text("Primary content")))
          .addCell(dataListCell().text("Secondary content")))
      .addItem(dataListItem("dl-compact-1")
          .addCell(dataListCell()
              .add(span().id("dl-compact-1").text("Second row primary")))
          .addCell(dataListCell().text("Second row secondary")))
      .addItem(dataListItem("dl-compact-2")
          .addCell(dataListCell()
              .add(span().id("dl-compact-2").text("Third row primary")))
          .addCell(dataListCell().text("Third row secondary")));
}
