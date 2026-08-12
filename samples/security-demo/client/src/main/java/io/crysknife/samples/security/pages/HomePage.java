/*
 * Copyright (C) 2026 treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.crysknife.samples.security.pages;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.DefaultPage;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.security.IfRole;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(role = DefaultPage.class)
@Templated("HomePage.html")
public class HomePage implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  @IfRole("user")
  HTMLDivElement userSection;

  @Inject
  @DataField
  @IfRole("admin")
  HTMLDivElement adminPanel;

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
