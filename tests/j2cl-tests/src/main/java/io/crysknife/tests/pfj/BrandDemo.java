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
import org.patternfly.component.brand.Brand;

import static org.patternfly.component.brand.Brand.brand;

@Page(path = "BrandDemo")
@Singleton
@Templated("BrandDemo.html")
public class BrandDemo implements IsElement<HTMLDivElement> {

  @DataField
  Brand basicBrand = brand("https://www.patternfly.org/assets/images/pf_logo.svg", "PatternFly logo");

  @DataField
  Brand sizedBrand = brand("https://www.patternfly.org/assets/images/pf_logo.svg", "PatternFly logo")
      .widths("200px")
      .heights("auto");
}
