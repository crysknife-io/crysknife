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

package io.crysknife.ui.security;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecurityContext {

  private User currentUser = User.ANONYMOUS;
  private final List<Runnable> changeListeners = new ArrayList<>();

  public User getUser() {
    return currentUser;
  }

  public void setUser(User user) {
    this.currentUser = user != null ? user : User.ANONYMOUS;
    changeListeners.forEach(Runnable::run);
  }

  public void addChangeListener(Runnable listener) {
    changeListeners.add(listener);
  }

  public boolean isLoggedIn() {
    return !User.ANONYMOUS.equals(currentUser);
  }

  public boolean isUserInRole(String role) {
    return currentUser.hasRole(role);
  }

  public boolean isUserInAllRoles(String... roles) {
    return currentUser.hasAllRoles(roles);
  }
}
