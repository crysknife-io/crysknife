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
import org.patternfly.component.badge.Badge;

import static org.patternfly.component.badge.Badge.badge;

@Page(path = "BadgeDemo")
@Singleton
@Templated("BadgeDemo.html")
public class BadgeDemo implements IsElement<HTMLDivElement> {

  @DataField
  Badge readBadge1 = badge(7).read();

  @DataField
  Badge readBadge2 = badge(24).read();

  @DataField
  Badge readBadge3 = badge(423).read();

  @DataField
  Badge readBadgeLimited = badge(1000).limit(999).read();

  @DataField
  Badge unreadBadge1 = badge(7).unread();

  @DataField
  Badge unreadBadge2 = badge(24).unread();

  @DataField
  Badge unreadBadge3 = badge(423).unread();

  @DataField
  Badge unreadBadgeLimited = badge(1000).limit(999).unread();
}
