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
import org.patternfly.component.form.TextArea;

import static org.patternfly.component.form.TextArea.textArea;
import static org.patternfly.component.form.TextAreaResize.both;
import static org.patternfly.component.form.TextAreaResize.vertical;

@Page(path = "TextAreaDemo")
@Singleton
@Templated("NumberInputDemo.html")
public class NumberInputDemo implements IsElement<HTMLDivElement> {

  @DataField
  TextArea basicTextArea = textArea("basic-text-area-0");

  @DataField
  TextArea verticalTextArea = textArea("vertical-text-area-0").resize(vertical);

  @DataField
  TextArea bothTextArea = textArea("both-text-area-0").resize(both);

  @DataField
  TextArea disabledTextArea = textArea("disabled-text-area-0").disabled();
}
