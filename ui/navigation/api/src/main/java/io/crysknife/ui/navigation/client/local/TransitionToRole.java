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

package io.crysknife.ui.navigation.client.local;

/**
 * This class works like a {@link TransitionTo} but where the target is a {@link UniquePageRole}. By
 * injecting and instance of this class you declare a compile-time dependency on the existence of a
 * {@link Page} with the {@link UniquePageRole} of type {@code U}.
 * 
 * @param <U> The type of {@link UniquePageRole} that this transition navigates to.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public final class TransitionToRole<U extends UniquePageRole> {

  private Class<U> uniquePageRole;

  private Navigation navigation;

  public TransitionToRole(final Navigation navigation, final Class<U> uniquePageRole) {
    this.uniquePageRole = uniquePageRole;
    this.navigation = navigation;
  }

  public void go() {
    navigation.goToWithRole(uniquePageRole);
  }

  public Class<U> toUniquePageRole() {
    return uniquePageRole;
  }
}
