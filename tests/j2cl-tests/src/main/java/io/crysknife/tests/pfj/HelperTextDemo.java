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
import org.patternfly.component.help.HelperText;

import static org.patternfly.component.ValidationStatus.error;
import static org.patternfly.component.ValidationStatus.success;
import static org.patternfly.component.ValidationStatus.warning;
import static org.patternfly.component.help.HelperText.helperText;
import static org.patternfly.component.help.HelperTextItem.helperTextItem;

@Page(path = "HelperTextDemo")
@Singleton
@Templated("HelperTextDemo.html")
public class HelperTextDemo implements IsElement<HTMLDivElement> {

  @DataField
  HelperText defaultHelper = helperText("This is default helper text");

  @DataField
  HelperText warningHelper = helperText("This is warning helper text", warning);

  @DataField
  HelperText successHelper = helperText("This is success helper text", success);

  @DataField
  HelperText errorHelper = helperText("This is error helper text", error);

  @DataField
  HelperText multipleHelper = helperText()
      .addItem(helperTextItem("This is default helper text"))
      .addItem(helperTextItem("This is another default helper text in the same block"))
      .addItem(helperTextItem("And this is more default text in the same block"));

  @DataField
  HelperText dynamicHelper = helperText()
      .addItem(helperTextItem("This is success helper text", success).dynamic())
      .addItem(helperTextItem("This is warning helper text", warning).dynamic())
      .addItem(helperTextItem("This is error helper text", error).dynamic());
}
