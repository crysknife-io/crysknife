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
import org.patternfly.component.modal.Modal;
import org.patternfly.style.Size;

import static org.jboss.elemento.EventType.click;
import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.modal.Modal.modal;
import static org.patternfly.component.modal.ModalBody.modalBody;
import static org.patternfly.component.modal.ModalFooter.modalFooter;

@Page(path = "ModalDemo")
@Singleton
@Templated("ModalDemo.html")
public class ModalDemo implements IsElement<HTMLDivElement> {

  @DataField
  Button openBasicBtn = button().primary().text("Open basic modal");

  @DataField
  Button openSmallBtn = button().primary().text("Open small modal");

  @PostConstruct
  void init() {
    Modal basicModal = modal()
        .addHeader("Basic modal title")
        .addBody(modalBody()
            .text("Lorem ipsum dolor sit amet, consectetur adipiscing elit."))
        .addFooter(modalFooter()
            .addButton(button().primary().text("Confirm"))
            .addButton(button().link().text("Cancel")))
        .appendToBody();

    openBasicBtn.on(click, e -> basicModal.open());

    Modal smallModal = modal()
        .size(Size.sm)
        .addHeader("Small modal title")
        .addBody(modalBody()
            .text("This is a small modal."))
        .addFooter(modalFooter()
            .addButton(button().primary().text("OK")))
        .appendToBody();

    openSmallBtn.on(click, e -> smallModal.open());
  }
}
