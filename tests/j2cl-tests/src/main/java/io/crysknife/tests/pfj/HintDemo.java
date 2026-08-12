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
import org.patternfly.component.hint.Hint;

import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.hint.Hint.hint;
import static org.patternfly.component.hint.HintBody.hintBody;
import static org.patternfly.component.hint.HintFooter.hintFooter;
import static org.patternfly.component.hint.HintTitle.hintTitle;

@Page(path = "HintDemo")
@Singleton
@Templated("HintDemo.html")
public class HintDemo implements IsElement<HTMLDivElement> {

  @DataField
  Hint basicHint = hint()
      .addTitle(hintTitle().text("Do more with Find it Fix it capabilities"))
      .addBody(hintBody().text(
          "Upgrade to Red Hat Smart Management to remediate all your systems across regions and geographies."))
      .addFooter(hintFooter()
          .add(button().link().inline().text("Try it for 90 days")));

  @DataField
  Hint noTitleHint = hint()
      .addBody(hintBody()
          .add("Welcome to the new documentation experience. ")
          .add(button().link().inline().text("Learn more about the improved features.")));
}
