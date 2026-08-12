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
import org.patternfly.component.accordion.Accordion;

import static org.jboss.elemento.Elements.p;
import static org.patternfly.component.accordion.Accordion.accordion;
import static org.patternfly.component.accordion.AccordionItem.accordionItem;

@Page(path = "AccordionDemo")
@Singleton
@Templated("AccordionDemo.html")
public class AccordionDemo implements IsElement<HTMLDivElement> {

  @DataField
  Accordion singleExpand = accordion().singleExpand()
      .addItem(accordionItem("se-0", "Item one").expanded()
          .add(p().text("This is the first accordion item content. It demonstrates the single-expand behavior where only one item can be open at a time.")))
      .addItem(accordionItem("se-1", "Item two")
          .add(p().text("This is the second accordion item. Opening this will close the first item.")))
      .addItem(accordionItem("se-2", "Item three")
          .add(p().text("This is the third accordion item with some more content to show.")))
      .addItem(accordionItem("se-3", "Item four")
          .add(p().text("The fourth item in the accordion. Single expand means only one is visible.")));

  @DataField
  Accordion borderedAccordion = accordion().singleExpand().bordered()
      .addItem(accordionItem("bd-0", "Bordered item one").expanded()
          .add(p().text("Bordered accordion items have a visible border around them for better visual separation.")))
      .addItem(accordionItem("bd-1", "Bordered item two")
          .add(p().text("This bordered item demonstrates the alternate visual style.")))
      .addItem(accordionItem("bd-2", "Bordered item three")
          .add(p().text("Bordered accordions work well when you need clear content boundaries.")));

  @DataField
  Accordion multiExpand = accordion().fixed()
      .addItem(accordionItem("me-0", "Multi-expand one").expanded()
          .add(p().text("With multi-expand, multiple items can be open simultaneously.")))
      .addItem(accordionItem("me-1", "Multi-expand two").expanded()
          .add(p().text("This item is also expanded by default along with the first one.")))
      .addItem(accordionItem("me-2", "Multi-expand three")
          .add(p().text("Click to expand this one too — it won't close the others.")));
}
