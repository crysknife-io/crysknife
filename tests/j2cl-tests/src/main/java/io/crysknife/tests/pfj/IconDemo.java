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
import org.patternfly.component.icon.Icon;
import org.patternfly.component.icon.IconSize;
import org.patternfly.style.Status;

import static org.patternfly.component.icon.Icon.icon;
import static org.patternfly.icon.IconSets.fas.home;
import static org.patternfly.icon.IconSets.fas.user;
import static org.patternfly.icon.IconSets.fas.check;
import static org.patternfly.icon.IconSets.fas.exclamationTriangle;
import static org.patternfly.icon.IconSets.fas.exclamationCircle;
import static org.patternfly.icon.IconSets.fas.infoCircle;
import static org.patternfly.icon.IconSets.fas.cog;
import static org.patternfly.icon.IconSets.fas.bell;

@Page(path = "IconDemo")
@Singleton
@Templated("IconDemo.html")
public class IconDemo implements IsElement<HTMLDivElement> {

  @DataField
  Icon smIcon = icon(home()).size(IconSize.sm);

  @DataField
  Icon mdIcon = icon(user()).size(IconSize.md);

  @DataField
  Icon lgIcon = icon(cog()).size(IconSize.lg);

  @DataField
  Icon xlIcon = icon(bell()).size(IconSize.xl);

  @DataField
  Icon successIcon = icon(check()).status(Status.success);

  @DataField
  Icon warningIcon = icon(exclamationTriangle()).status(Status.warning);

  @DataField
  Icon dangerIcon = icon(exclamationCircle()).status(Status.danger);

  @DataField
  Icon infoIcon = icon(infoCircle()).status(Status.info);

  @DataField
  Icon inlineIcon = icon(home()).inline();
}
