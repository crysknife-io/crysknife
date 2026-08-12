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
import org.patternfly.component.table.Table;

import static org.patternfly.component.table.Table.table;
import static org.patternfly.component.table.Tbody.tbody;
import static org.patternfly.component.table.Td.td;
import static org.patternfly.component.table.Th.th;
import static org.patternfly.component.table.Thead.thead;
import static org.patternfly.component.table.Tr.tr;

@Page(path = "TableDemo")
@Singleton
@Templated("TableDemo.html")
public class TableDemo implements IsElement<HTMLDivElement> {

  @DataField
  Table basicTable = table().ariaLabel("Basic table")
      .addHead(thead()
          .addRow(tr("head")
              .add(th("name").text("Name"))
              .add(th("version").text("Version"))
              .add(th("status").text("Status"))
              .add(th("location").text("Location"))))
      .addBody(tbody()
          .addRow(tr("row-1")
              .add(td("name").text("PatternFly"))
              .add(td("version").text("6.0"))
              .add(td("status").text("Active"))
              .add(td("location").text("Westford, MA")))
          .addRow(tr("row-2")
              .add(td("name").text("Crysknife"))
              .add(td("version").text("0.10"))
              .add(td("status").text("Active"))
              .add(td("location").text("Remote")))
          .addRow(tr("row-3")
              .add(td("name").text("J2CL"))
              .add(td("version").text("0.11"))
              .add(td("status").text("Active"))
              .add(td("location").text("Mountain View, CA")))
          .addRow(tr("row-4")
              .add(td("name").text("Elemento"))
              .add(td("version").text("1.6"))
              .add(td("status").text("Active"))
              .add(td("location").text("Remote"))));

  @DataField
  Table compactTable = table().ariaLabel("Compact table").compact()
      .addHead(thead()
          .addRow(tr("compact-head")
              .add(th("repo").text("Repository"))
              .add(th("stars").text("Stars"))
              .add(th("forks").text("Forks"))
              .add(th("lang").text("Language"))))
      .addBody(tbody()
          .addRow(tr("compact-1")
              .add(td("repo").text("patternfly-java"))
              .add(td("stars").text("42"))
              .add(td("forks").text("12"))
              .add(td("lang").text("Java")))
          .addRow(tr("compact-2")
              .add(td("repo").text("crysknife"))
              .add(td("stars").text("38"))
              .add(td("forks").text("8"))
              .add(td("lang").text("Java")))
          .addRow(tr("compact-3")
              .add(td("repo").text("elemento"))
              .add(td("stars").text("156"))
              .add(td("forks").text("24"))
              .add(td("lang").text("Java"))));
}
