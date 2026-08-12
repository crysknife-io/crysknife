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
import org.patternfly.component.truncate.Truncate;

import static org.patternfly.component.truncate.Truncate.truncate;
import static org.patternfly.component.truncate.TruncatePosition.middle;
import static org.patternfly.component.truncate.TruncatePosition.start;

@Page(path = "TruncateDemo")
@Singleton
@Templated("TruncateDemo.html")
public class TruncateDemo implements IsElement<HTMLDivElement> {

  private static final String LONG_TEXT =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt "
          + "ut labore et dolore magna aliqua.";

  @DataField
  Truncate defaultTruncate = truncate(LONG_TEXT);

  @DataField
  Truncate middleTruncate = truncate(
      "redhat_logo_black_and_white_reversed_simple_with_fedora_container.zip", middle);

  @DataField
  Truncate startTruncate = truncate(LONG_TEXT, start);
}
