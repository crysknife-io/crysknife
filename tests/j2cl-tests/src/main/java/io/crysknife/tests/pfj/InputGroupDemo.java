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
import org.patternfly.component.inputgroup.InputGroup;

import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.form.TextInput.textInput;
import static org.patternfly.component.inputgroup.InputGroup.inputGroup;
import static org.patternfly.component.inputgroup.InputGroupItem.inputGroupItem;
import static org.patternfly.component.inputgroup.InputGroupText.inputGroupText;

@Page(path = "InputGroupDemo")
@Singleton
@Templated("InputGroupDemo.html")
public class InputGroupDemo implements IsElement<HTMLDivElement> {

  @DataField
  InputGroup basicInputGroup = inputGroup()
      .addItem(inputGroupItem().addControl(textInput("basic-input")))
      .addText(inputGroupText("@example.com"));

  @DataField
  InputGroup buttonInputGroup = inputGroup()
      .addItem(inputGroupItem().addControl(textInput("search-input")).fill())
      .addItem(inputGroupItem().addButton(button().control().text("Search")));
}
