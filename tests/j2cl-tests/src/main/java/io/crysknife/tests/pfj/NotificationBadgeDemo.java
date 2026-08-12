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
import org.patternfly.component.notification.badge.NotificationBadge;

import static org.patternfly.component.notification.badge.NotificationBadge.notificationBadge;

@Page(path = "NotificationBadgeDemo")
@Singleton
@Templated("NotificationBadgeDemo.html")
public class NotificationBadgeDemo implements IsElement<HTMLDivElement> {

  @DataField
  NotificationBadge readBadge = notificationBadge()
      .read()
      .ariaLabel("No notifications");

  @DataField
  NotificationBadge unreadBadge = notificationBadge()
      .unread()
      .count(3)
      .ariaLabel("3 unread notifications");

  @DataField
  NotificationBadge attentionBadge = notificationBadge()
      .attention()
      .count(7)
      .ariaLabel("7 notifications need attention");

  @DataField
  NotificationBadge highCountBadge = notificationBadge()
      .unread()
      .count(99)
      .ariaLabel("99 unread notifications");
}
