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
import org.patternfly.component.form.Checkbox;

import static org.patternfly.component.form.Checkbox.checkbox;
import static org.patternfly.component.form.CheckboxDescription.checkboxDescription;
import static org.patternfly.component.form.CheckboxBody.checkboxBody;

@Page(path = "CheckboxDemo")
@Singleton
@Templated("CheckboxDemo.html")
public class CheckboxDemo implements IsElement<HTMLDivElement> {

  @DataField
  Checkbox basicChecked = checkbox("basic-0", "basic", "Checked by default").value(true);

  @DataField
  Checkbox basicUnchecked = checkbox("basic-1", "basic", "Unchecked by default");

  @DataField
  Checkbox disabledChecked = checkbox("disabled-0", "disabled", "Disabled checked")
      .disabled().value(true);

  @DataField
  Checkbox disabledUnchecked = checkbox("disabled-1", "disabled", "Disabled unchecked")
      .disabled();

  @DataField
  Checkbox withDescription = checkbox("desc-0", "desc", "Checkbox with description")
      .addDescription(checkboxDescription(
          "This is a description that provides additional context about what this checkbox controls."));

  @DataField
  Checkbox withBody = checkbox("body-0", "body", "Checkbox with body")
      .addBody(checkboxBody("This is additional body content below the checkbox."));

  @DataField
  Checkbox withDescAndBody = checkbox("both-0", "both", "Checkbox with description and body")
      .addDescription(checkboxDescription("A description appears directly below the label."))
      .addBody(checkboxBody("Body content appears below the description."));
}
