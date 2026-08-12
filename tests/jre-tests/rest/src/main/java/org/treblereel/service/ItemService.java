/*
 * Copyright © 2020 Treblereel
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

package org.treblereel.service;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.treblereel.model.Item;

@Path("/api/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ItemService {

    @GET
    @Path("/{id}")
    Item getItem(@PathParam("id") long id);

    @GET
    List<Item> listItems();

    @POST
    Item createItem(Item item);

    @PUT
    @Path("/{id}")
    Item updateItem(@PathParam("id") long id, Item item);

    @DELETE
    @Path("/{id}")
    Item deleteItem(@PathParam("id") long id);

    @PATCH
    @Path("/{id}")
    Item patchItem(@PathParam("id") long id, Item item);

    @DELETE
    @Path("/void/{id}")
    void deleteItemVoid(@PathParam("id") long id);

    @GET
    @Path("/search")
    List<Item> searchItems(@QueryParam("name") String name);

    @GET
    @Path("/search-default")
    List<Item> searchItemsDefaultPage(@QueryParam("page") @DefaultValue("0") String page);

    @GET
    @Path("/{id}/header")
    Item getItemWithHeader(@PathParam("id") long id,
            @HeaderParam("X-Custom-Header") String customHeader);

    @GET
    @Path("/{id}/cookie")
    Item getItemWithCookie(@PathParam("id") long id, @CookieParam("session") String session);

    @POST
    @Path("/form")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Item createItemFromForm(@FormParam("id") long id, @FormParam("name") String name);

    @GET
    @Path("/{a}/related/{b}")
    Item getRelatedItem(@PathParam("a") long a, @PathParam("b") long b);

    @GET
    @Path("/auth-echo")
    Item getAuthEcho();

    @GET
    @Path("/protected")
    Item getProtected();

    @GET
    @Path("/error/404")
    Item getError404();

    @GET
    @Path("/error/500")
    Item getError500();
}
