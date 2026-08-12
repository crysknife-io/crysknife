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
import org.patternfly.component.avatar.Avatar;

import static org.patternfly.component.avatar.Avatar.avatar;
import static org.patternfly.style.Size.lg;
import static org.patternfly.style.Size.sm;
import static org.patternfly.style.Size.xl;

@Page(path = "AvatarDemo")
@Singleton
@Templated("AvatarDemo.html")
public class AvatarDemo implements IsElement<HTMLDivElement> {

  @DataField
  Avatar basicAvatar = avatar("https://www.patternfly.org/images/avatarImg.svg", "avatar");

  @DataField
  Avatar smallAvatar = avatar("https://www.patternfly.org/images/avatarImg.svg", "avatar").size(sm);

  @DataField
  Avatar largeAvatar = avatar("https://www.patternfly.org/images/avatarImg.svg", "avatar").size(lg);

  @DataField
  Avatar xlAvatar = avatar("https://www.patternfly.org/images/avatarImg.svg", "avatar").size(xl);

  @DataField
  Avatar borderedAvatar = avatar("https://www.patternfly.org/images/avatarImg.svg", "avatar").bordered();
}
