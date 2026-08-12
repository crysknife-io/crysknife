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
import org.patternfly.component.form.TextInput;

import static org.patternfly.component.ValidationStatus.error;
import static org.patternfly.component.form.TextInput.textInput;
import static org.patternfly.icon.IconSets.fas.calendar;
import static org.patternfly.icon.IconSets.fas.clock;

@Page(path = "TextInputDemo")
@Singleton
@Templated("TextInputDemo.html")
public class TextInputDemo implements IsElement<HTMLDivElement> {

  @DataField
  TextInput basicInput = textInput("basic-input-0");

  @DataField
  TextInput disabledInput = textInput("disabled-input-0", "disabled text input")
      .disabled();

  @DataField
  TextInput readonlyInput = textInput("readonly-input-0", "read only text input")
      .readonly();

  @DataField
  TextInput invalidInput = textInput("invalid-input-0").validated(error);

  @DataField
  TextInput calendarInput = textInput("calendar-input-0").icon(calendar());

  @DataField
  TextInput clockInput = textInput("clock-input-0").icon(clock());
}
