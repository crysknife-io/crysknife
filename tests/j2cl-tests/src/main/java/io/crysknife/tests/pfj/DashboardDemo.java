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
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.patternfly.component.badge.Badge;
import org.patternfly.component.card.Card;
import org.patternfly.component.list.DescriptionList;
import org.patternfly.component.list.DataList;
import org.patternfly.component.progress.Progress;

import static org.jboss.elemento.Elements.span;
import static org.patternfly.component.badge.Badge.badge;
import static org.patternfly.component.card.Card.card;
import static org.patternfly.component.card.CardBody.cardBody;
import static org.patternfly.component.card.CardFooter.cardFooter;
import static org.patternfly.component.card.CardTitle.cardTitle;
import static org.patternfly.component.label.Label.label;
import static org.patternfly.component.list.DataList.dataList;
import static org.patternfly.component.list.DataListCell.dataListCell;
import static org.patternfly.component.list.DataListItem.dataListItem;
import static org.patternfly.component.list.DescriptionList.descriptionList;
import static org.patternfly.component.list.DescriptionListDescription.descriptionListDescription;
import static org.patternfly.component.list.DescriptionListGroup.descriptionListGroup;
import static org.patternfly.component.list.DescriptionListTerm.descriptionListTerm;
import static org.patternfly.component.progress.Progress.progress;
import static org.patternfly.style.Color.blue;
import static org.patternfly.style.Color.green;
import static org.patternfly.style.Color.red;
import static org.patternfly.style.Status.danger;
import static org.patternfly.style.Status.success;
import static org.patternfly.style.Status.warning;

@Page(path = "DashboardDemo")
@Singleton
@Templated("DashboardDemo.html")
public class DashboardDemo implements IsElement<HTMLDivElement> {

  @DataField
  Card detailsCard = card()
      .addTitle(cardTitle("Cluster details"))
      .addBody(cardBody()
          .add(descriptionList().compact()
              .addItem(descriptionListGroup("dd-name")
                  .addTerm(descriptionListTerm("Name"))
                  .addDescription(descriptionListDescription("production-cluster-01")))
              .addItem(descriptionListGroup("dd-provider")
                  .addTerm(descriptionListTerm("Provider"))
                  .addDescription(descriptionListDescription("AWS")))
              .addItem(descriptionListGroup("dd-os")
                  .addTerm(descriptionListTerm("OS"))
                  .addDescription(descriptionListDescription("RHEL 9.2")))
              .addItem(descriptionListGroup("dd-version")
                  .addTerm(descriptionListTerm("Version"))
                  .addDescription(descriptionListDescription("4.14.6")))));

  @DataField
  Card statusCard = card()
      .addTitle(cardTitle("Status"))
      .addBody(cardBody()
          .add(descriptionList().compact().horizontal()
              .addItem(descriptionListGroup("ds-nodes")
                  .addTerm(descriptionListTerm("Nodes"))
                  .addDescription(descriptionListDescription()
                      .add(label("6 Running", green).filled())))
              .addItem(descriptionListGroup("ds-pods")
                  .addTerm(descriptionListTerm("Pods"))
                  .addDescription(descriptionListDescription()
                      .add(label("142 Running", green).filled())))
              .addItem(descriptionListGroup("ds-alerts")
                  .addTerm(descriptionListTerm("Alerts"))
                  .addDescription(descriptionListDescription()
                      .add(label("2 Warning", red).filled())))));

  @DataField
  Card utilizationCard = card()
      .addTitle(cardTitle("Utilization"))
      .addBody(cardBody()
          .add(progress().title("CPU").value(62).status(warning))
          .add(progress().title("Memory").value(78).status(warning))
          .add(progress().title("Storage").value(45).status(success)));

  @DataField
  Card inventoryCard = card()
      .addTitle(cardTitle("Inventory"))
      .addBody(cardBody()
          .add(descriptionList().horizontal().compact()
              .addItem(descriptionListGroup("di-nodes")
                  .addTerm(descriptionListTerm("Nodes"))
                  .addDescription(descriptionListDescription()
                      .add(badge(6).read())))
              .addItem(descriptionListGroup("di-pods")
                  .addTerm(descriptionListTerm("Pods"))
                  .addDescription(descriptionListDescription()
                      .add(badge(142).read())))
              .addItem(descriptionListGroup("di-services")
                  .addTerm(descriptionListTerm("Services"))
                  .addDescription(descriptionListDescription()
                      .add(badge(23).read())))
              .addItem(descriptionListGroup("di-pvcs")
                  .addTerm(descriptionListTerm("PVCs"))
                  .addDescription(descriptionListDescription()
                      .add(badge(18).read())))
              .addItem(descriptionListGroup("di-routes")
                  .addTerm(descriptionListTerm("Routes"))
                  .addDescription(descriptionListDescription()
                      .add(badge(9).read())))));

  @DataField
  Card eventsCard = card()
      .addTitle(cardTitle("Events"))
      .addBody(cardBody()
          .add(dataList().compact()
              .addItem(dataListItem("ev-0")
                  .addCell(dataListCell()
                      .add(span().text("Pod crysknife-api-7b8d5f restarted")))
                  .addCell(dataListCell()
                      .add(label("Warning", red).outline())))
              .addItem(dataListItem("ev-1")
                  .addCell(dataListCell()
                      .add(span().text("Deployment config-service scaled to 3")))
                  .addCell(dataListCell()
                      .add(label("Info", blue).outline())))
              .addItem(dataListItem("ev-2")
                  .addCell(dataListCell()
                      .add(span().text("Build crysknife-ui #42 completed")))
                  .addCell(dataListCell()
                      .add(label("Success", green).outline())))
              .addItem(dataListItem("ev-3")
                  .addCell(dataListCell()
                      .add(span().text("Node ip-10-0-1-42 memory pressure")))
                  .addCell(dataListCell()
                      .add(label("Warning", red).outline())))))
      .addFooter(cardFooter().text("View all events"));

  @EventHandler("eventsCard")
  public void onEventsCardClick(@ForEvent("click") final MouseEvent e) {
  }
}
