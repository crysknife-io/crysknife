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

package io.crysknife.samples.security.server;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;

@Path("/api")
public class UserResource {

  @Inject
  SecurityIdentity identity;

  @GET
  @Path("/userinfo")
  @PermitAll
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUserInfo() {
    if (identity.isAnonymous()) {
      return Response.ok(Map.of("anonymous", true))
          .build();
    }
    String name = identity.getPrincipal().getName();
    Set<String> roles = identity.getRoles();
    return Response.ok(Map.of("name", name, "roles", roles))
        .build();
  }

  @GET
  @Path("/login")
  @Authenticated
  public Response login() {
    return Response.seeOther(URI.create("/")).build();
  }

}
