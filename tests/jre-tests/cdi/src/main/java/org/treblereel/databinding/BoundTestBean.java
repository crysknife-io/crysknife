/*
 * Copyright © 2025 Treblereel
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
package org.treblereel.databinding;

import elemental2.dom.HTMLElement;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import io.crysknife.ui.databinding.annotation.Bound;
import io.crysknife.ui.databinding.api.DataBinder;

@Dependent
public class BoundTestBean {

  @Inject
  public DataBinder<UserModel> binder;

  @Bound(property = "name")
  public HTMLElement nameField;

  @Bound(property = "email")
  public HTMLElement emailField;
}
