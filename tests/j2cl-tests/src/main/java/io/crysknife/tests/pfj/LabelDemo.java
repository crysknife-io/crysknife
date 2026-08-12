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
import org.patternfly.component.label.Label;

import static org.patternfly.component.label.Label.label;
import static org.patternfly.style.Color.blue;
import static org.patternfly.style.Color.green;
import static org.patternfly.style.Color.orange;
import static org.patternfly.style.Color.purple;
import static org.patternfly.style.Color.red;

@Page(path = "LabelDemo")
@Singleton
@Templated("LabelDemo.html")
public class LabelDemo implements IsElement<HTMLDivElement> {

  @DataField
  Label blueLabel = label("Blue", blue).filled();

  @DataField
  Label greenLabel = label("Green", green).filled();

  @DataField
  Label orangeLabel = label("Orange", orange).filled();

  @DataField
  Label redLabel = label("Red", red).filled();

  @DataField
  Label purpleLabel = label("Purple", purple).filled();

  @DataField
  Label outlineBlue = label("Blue outline", blue).outline();

  @DataField
  Label outlineGreen = label("Green outline", green).outline();

  @DataField
  Label outlineOrange = label("Orange outline", orange).outline();

  @DataField
  Label outlineRed = label("Red outline", red).outline();

  @DataField
  Label closableLabel = label("Closable", blue).filled().closable();
}
