/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package io.crysknife.ui.navigation.client.local.api;

import io.crysknife.ui.navigation.client.local.PageRole;
import io.crysknife.ui.navigation.client.local.UniquePageRole;

/**
 * Thrown when navigation by {@link PageRole} is attempted and no page with that role exists.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class MissingPageRoleException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public MissingPageRoleException(final Class<? extends UniquePageRole> pageRole) {
    super("No page was found with the given role: " + pageRole.getName());
  }
}
