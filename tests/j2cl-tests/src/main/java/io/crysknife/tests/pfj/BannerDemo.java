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
import org.patternfly.component.banner.Banner;

import static org.patternfly.component.banner.Banner.banner;
import static org.patternfly.style.Color.blue;
import static org.patternfly.style.Color.green;
import static org.patternfly.style.Color.red;
import static org.patternfly.style.Color.purple;
import static org.patternfly.style.Color.yellow;

@Page(path = "BannerDemo")
@Singleton
@Templated("BannerDemo.html")
public class BannerDemo implements IsElement<HTMLDivElement> {

  @DataField
  Banner defaultBanner = banner("Default banner");

  @DataField
  Banner redBanner = banner("Red banner", red);

  @DataField
  Banner yellowBanner = banner("Yellow banner", yellow);

  @DataField
  Banner greenBanner = banner("Green banner", green);

  @DataField
  Banner blueBanner = banner("Blue banner", blue);

  @DataField
  Banner purpleBanner = banner("Purple banner", purple);
}
