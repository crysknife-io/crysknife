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

package io.crysknife.tests.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import jakarta.ws.rs.core.Response;

import io.crysknife.tests.rest.shared.model.Category;
import io.crysknife.tests.rest.shared.model.DetailedItem;
import io.crysknife.tests.rest.shared.model.Item;

@Path("/api/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private static final List<Item> STORE = new ArrayList<>();

    static {
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("item-1");
        STORE.add(item1);
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("item-2");
        STORE.add(item2);
    }

    @GET
    @Path("/{id}")
    public Item getItem(@PathParam("id") long id) {
        return STORE.stream()
            .filter(i -> i.getId() == id)
            .findFirst()
            .orElseGet(() -> {
                Item item = new Item();
                item.setId(id);
                item.setName("item-" + id);
                return item;
            });
    }

    @GET
    public List<Item> listItems() {
        return new ArrayList<>(STORE);
    }

    @POST
    public Response createItem(Item item) {
        return Response.status(Response.Status.CREATED).entity(item).build();
    }

    @PUT
    @Path("/{id}")
    public Item updateItem(@PathParam("id") long id, Item item) {
        item.setId(id);
        return item;
    }

    @DELETE
    @Path("/{id}")
    public Item deleteItem(@PathParam("id") long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("deleted-" + id);
        return item;
    }

    @GET
    @Path("/search")
    public List<Item> searchItems(@QueryParam("name") String name) {
        return STORE.stream()
            .filter(i -> i.getName().contains(name))
            .collect(Collectors.toList());
    }

    @GET
    @Path("/detailed/{id}")
    public DetailedItem getDetailedItem(@PathParam("id") long id) {
        Category category = new Category();
        category.setId(10);
        category.setName("electronics");
        DetailedItem item = new DetailedItem();
        item.setId(id);
        item.setName("detailed-" + id);
        item.setCategory(category);
        return item;
    }

    @DELETE
    @Path("/void/{id}")
    public Response deleteItemVoid(@PathParam("id") long id) {
        return Response.noContent().build();
    }

    @GET
    @Path("/error/404")
    public Response getError404() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/error/500")
    public Response getError500() {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
