/*
 * Copyright © 2026 Treblereel
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
package io.crysknife.tests.rest.shared.service;

import java.util.List;

import io.crysknife.tests.rest.shared.model.DetailedItem;
import io.crysknife.tests.rest.shared.model.Item;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/items")
public interface ItemService {

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  Item getItem(@PathParam("id") long id);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  List<Item> listItems();

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Item createItem(Item item);

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Item updateItem(@PathParam("id") long id, Item item);

  @DELETE
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  Item deleteItem(@PathParam("id") long id);

  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  List<Item> searchItems(@QueryParam("name") String name);

  @GET
  @Path("/detailed/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  DetailedItem getDetailedItem(@PathParam("id") long id);

  @DELETE
  @Path("/void/{id}")
  void deleteItemVoid(@PathParam("id") long id);

  @GET
  @Path("/error/404")
  @Produces(MediaType.APPLICATION_JSON)
  Item getError404();

  @GET
  @Path("/error/500")
  @Produces(MediaType.APPLICATION_JSON)
  Item getError500();
}
