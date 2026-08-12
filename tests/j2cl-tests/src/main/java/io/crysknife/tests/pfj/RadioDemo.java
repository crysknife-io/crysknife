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
import org.patternfly.component.form.Radio;

import static org.patternfly.component.form.Radio.radio;
import static org.patternfly.component.form.RadioBody.radioBody;
import static org.patternfly.component.form.RadioDescription.radioDescription;

@Page(path = "RadioDemo")
@Singleton
@Templated("RadioDemo.html")
public class RadioDemo implements IsElement<HTMLDivElement> {

  @DataField
  Radio radio1 = radio("basic-radio-0", "basic-radio", "Option 1").value(true);

  @DataField
  Radio radio2 = radio("basic-radio-1", "basic-radio", "Option 2");

  @DataField
  Radio reversedRadio = radio("reversed-radio-0", "reversed-radio", "Reversed option").reversed();

  @DataField
  Radio disabledRadio1 = radio("disabled-radio-0", "disabled-radio", "Disabled checked")
      .value(true).disabled();

  @DataField
  Radio disabledRadio2 = radio("disabled-radio-1", "disabled-radio", "Disabled unchecked")
      .disabled();

  @DataField
  Radio descriptionRadio = radio("desc-radio-0", "desc-radio", "Radio with description")
      .addDescription(radioDescription("This is a longer description to provide more context."));

  @DataField
  Radio bodyRadio = radio("body-radio-0", "body-radio", "Radio with body")
      .addBody(radioBody("This is where custom content goes."));
}
