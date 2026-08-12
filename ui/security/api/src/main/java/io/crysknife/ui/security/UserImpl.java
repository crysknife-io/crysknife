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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserImpl implements User {

  private final String identifier;
  private final Set<Role> roles;

  public UserImpl(String identifier) {
    this(identifier, Collections.emptySet());
  }

  public UserImpl(String identifier, Set<Role> roles) {
    this.identifier = Objects.requireNonNull(identifier, "User identifier must not be null");
    this.roles = Collections.unmodifiableSet(new HashSet<>(roles));
  }

  public UserImpl(String identifier, String... roleNames) {
    this(identifier,
        Arrays.stream(roleNames).map(RoleImpl::new).collect(Collectors.toSet()));
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public Set<Role> getRoles() {
    return roles;
  }

  @Override
  public boolean hasRole(String roleName) {
    for (Role role : roles) {
      if (role.getName().equals(roleName)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasAllRoles(String... roleNames) {
    for (String roleName : roleNames) {
      if (!hasRole(roleName)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }
    return identifier.equals(((User) o).getIdentifier());
  }

  @Override
  public int hashCode() {
    return identifier.hashCode();
  }

  @Override
  public String toString() {
    return "User{" + identifier + ", roles=" + roles + "}";
  }
}
