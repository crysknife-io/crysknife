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
import org.patternfly.component.form.Form;

import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.form.Form.form;
import static org.patternfly.component.form.FormActionGroup.formActionGroup;
import static org.patternfly.component.form.FormGroup.formGroup;
import static org.patternfly.component.form.FormGroupControl.formGroupControl;
import static org.patternfly.component.form.FormGroupLabel.formGroupLabel;
import static org.patternfly.component.form.TextInput.textInput;

@Page(path = "FormDemo")
@Singleton
@Templated("FormDemo.html")
public class FormDemo implements IsElement<HTMLDivElement> {

  @DataField
  Form basicForm = form()
      .addGroup(formGroup("name")
          .addLabel(formGroupLabel("Full name"))
          .addControl(formGroupControl()
              .addControl(textInput("name"))))
      .addGroup(formGroup("email")
          .required()
          .addLabel(formGroupLabel("Email"))
          .addControl(formGroupControl()
              .addControl(textInput("email"))))
      .addActionGroup(formActionGroup()
          .addButton(button().primary().text("Submit"))
          .addButton(button().link().text("Cancel")));

  @DataField
  Form horizontalForm = form().horizontal()
      .addGroup(formGroup("h-name")
          .addLabel(formGroupLabel("Full name"))
          .addControl(formGroupControl()
              .addControl(textInput("h-name"))))
      .addGroup(formGroup("h-email")
          .required()
          .addLabel(formGroupLabel("Email"))
          .addControl(formGroupControl()
              .addControl(textInput("h-email"))))
      .addActionGroup(formActionGroup()
          .addButton(button().primary().text("Submit"))
          .addButton(button().link().text("Cancel")));
}
