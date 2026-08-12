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
import org.patternfly.style.Size;

import static org.jboss.elemento.Elements.div;
import static org.patternfly.component.spinner.Spinner.spinner;

@Page(path = "SpinnerDemo")
@Singleton
@Templated("SpinnerDemo2.html")
public class SpinnerDemo2 implements IsElement<HTMLDivElement> {

  @DataField
  HTMLDivElement spinnerContainer;

  @PostConstruct
  void init() {
    spinnerContainer.appendChild(div()
        .style("display", "flex")
        .style("gap", "1rem")
        .style("align-items", "center")
        .add(spinner(Size.sm))
        .add(spinner(Size.md))
        .add(spinner(Size.lg))
        .add(spinner(Size.xl))
        .element());
  }
}
