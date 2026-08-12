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
import org.patternfly.component.title.Title;

import static org.patternfly.component.title.Title.title;
import static org.patternfly.style.Size._2xl;
import static org.patternfly.style.Size._3xl;
import static org.patternfly.style.Size._4xl;
import static org.patternfly.style.Size.lg;
import static org.patternfly.style.Size.md;
import static org.patternfly.style.Size.xl;

@Page(path = "TitleDemo")
@Singleton
@Templated("TitleDemo.html")
public class TitleDemo implements IsElement<HTMLDivElement> {

  @DataField
  Title h1Title = title(1, "H1-styled title");

  @DataField
  Title h2Title = title(2, "H2-styled title");

  @DataField
  Title h3Title = title(3, "H3-styled title");

  @DataField
  Title h4Title = title(4, "H4-styled title");

  @DataField
  Title h5Title = title(5, "H5-styled title");

  @DataField
  Title h6Title = title(6, "H6-styled title");

  @DataField
  Title custom4xl = title(1, _4xl, "4xl title");

  @DataField
  Title custom3xl = title(2, _3xl, "3xl title");

  @DataField
  Title custom2xl = title(3, _2xl, "2xl title");

  @DataField
  Title customXl = title(4, xl, "xl title");

  @DataField
  Title customLg = title(5, lg, "lg title");

  @DataField
  Title customMd = title(6, md, "md title");
}
