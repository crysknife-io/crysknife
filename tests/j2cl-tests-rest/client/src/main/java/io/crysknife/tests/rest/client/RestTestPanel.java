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

package io.crysknife.tests.rest.client;

import java.util.List;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.tests.rest.shared.model.DetailedItem;
import io.crysknife.tests.rest.shared.model.Item;
import io.crysknife.tests.rest.shared.model.Post;
import io.crysknife.tests.rest.shared.service.ItemService;
import io.crysknife.tests.rest.shared.service.ItemService_RestCaller;
import io.crysknife.tests.rest.shared.service.PostService;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.treblereel.gwt.rest.client.Caller;

@Singleton
@Templated("resttestpanel.html")
public class RestTestPanel implements IsElement<HTMLDivElement> {

    @Inject
    Caller<ItemService> itemServiceCaller;

    @Inject
    @Named("jsonplaceholder")
    Caller<PostService> postServiceCaller;

    @Inject
    @ExternalApi
    Caller<PostService> externalApiCaller;

    @Inject
    @DataField
    HTMLDivElement root;

    @Inject
    @DataField
    HTMLButtonElement getItemBtn;

    @Inject
    @DataField
    HTMLDivElement getItemResult;

    @Inject
    @DataField
    HTMLButtonElement listItemsBtn;

    @Inject
    @DataField
    HTMLDivElement listItemsResult;

    @Inject
    @DataField
    HTMLButtonElement createItemBtn;

    @Inject
    @DataField
    HTMLDivElement createItemResult;

    @Inject
    @DataField
    HTMLButtonElement updateItemBtn;

    @Inject
    @DataField
    HTMLDivElement updateItemResult;

    @Inject
    @DataField
    HTMLButtonElement deleteItemBtn;

    @Inject
    @DataField
    HTMLDivElement deleteItemResult;

    @Inject
    @DataField
    HTMLButtonElement searchItemsBtn;

    @Inject
    @DataField
    HTMLDivElement searchItemsResult;

    @Inject
    @DataField
    HTMLButtonElement detailedItemBtn;

    @Inject
    @DataField
    HTMLDivElement detailedItemResult;

    @Inject
    @DataField
    HTMLButtonElement searchEmptyBtn;

    @Inject
    @DataField
    HTMLDivElement searchEmptyResult;

    @Inject
    @DataField
    HTMLButtonElement deleteVoidBtn;

    @Inject
    @DataField
    HTMLDivElement deleteVoidResult;

    @Inject
    @DataField
    HTMLButtonElement fullResponseBtn;

    @Inject
    @DataField
    HTMLDivElement fullResponseResult;

    @Inject
    @DataField
    HTMLButtonElement error404Btn;

    @Inject
    @DataField
    HTMLDivElement error404Result;

    @Inject
    @DataField
    HTMLButtonElement error500Btn;

    @Inject
    @DataField
    HTMLDivElement error500Result;

    @Inject
    @DataField
    HTMLButtonElement promiseGetItemBtn;

    @Inject
    @DataField
    HTMLDivElement promiseGetItemResult;

    @Inject
    @DataField
    HTMLButtonElement promiseListItemsBtn;

    @Inject
    @DataField
    HTMLDivElement promiseListItemsResult;

    @Inject
    @DataField
    HTMLButtonElement getPostBtn;

    @Inject
    @DataField
    HTMLDivElement getPostResult;

    @Inject
    @DataField
    HTMLButtonElement getPostCustomBtn;

    @Inject
    @DataField
    HTMLDivElement getPostCustomResult;

    @EventHandler("getItemBtn")
    public void onGetItem(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                getItemResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                Item item = (Item) r;
                getItemResult.textContent = item.getId() + ":" + item.getName();
            })
            .getItem(1);
    }

    @EventHandler("listItemsBtn")
    @SuppressWarnings("unchecked")
    public void onListItems(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                listItemsResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                List<Item> list = (List<Item>) r;
                listItemsResult.textContent = String.valueOf(list.size());
            })
            .listItems();
    }

    @EventHandler("createItemBtn")
    public void onCreateItem(@ForEvent("click") MouseEvent e) {
        Item newItem = new Item();
        newItem.setId(99);
        newItem.setName("new-item");
        itemServiceCaller
            .onError((response, throwable) -> {
                createItemResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                Item created = (Item) r;
                createItemResult.textContent = created.getId() + ":" + created.getName();
            })
            .createItem(newItem);
    }

    @EventHandler("updateItemBtn")
    public void onUpdateItem(@ForEvent("click") MouseEvent e) {
        Item updated = new Item();
        updated.setId(1);
        updated.setName("updated-item");
        itemServiceCaller
            .onError((response, throwable) -> {
                updateItemResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                Item item = (Item) r;
                updateItemResult.textContent = item.getId() + ":" + item.getName();
            })
            .updateItem(1, updated);
    }

    @EventHandler("deleteItemBtn")
    public void onDeleteItem(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                deleteItemResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                Item item = (Item) r;
                deleteItemResult.textContent = item.getId() + ":" + item.getName();
            })
            .deleteItem(1);
    }

    @EventHandler("searchItemsBtn")
    @SuppressWarnings("unchecked")
    public void onSearchItems(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                searchItemsResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                List<Item> list = (List<Item>) r;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(list.get(i).getId()).append(":").append(list.get(i).getName());
                }
                searchItemsResult.textContent = sb.toString();
            })
            .searchItems("item-1");
    }

    @EventHandler("detailedItemBtn")
    public void onDetailedItem(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                detailedItemResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                DetailedItem item = (DetailedItem) r;
                detailedItemResult.textContent = item.getId() + ":" + item.getName()
                    + ":" + item.getCategory().getId() + ":" + item.getCategory().getName();
            })
            .getDetailedItem(1);
    }

    @EventHandler("searchEmptyBtn")
    @SuppressWarnings("unchecked")
    public void onSearchEmpty(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                searchEmptyResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                List<Item> list = (List<Item>) r;
                searchEmptyResult.textContent = "EMPTY:" + list.size();
            })
            .searchItems("nonexistent");
    }

    @EventHandler("deleteVoidBtn")
    public void onDeleteVoid(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                deleteVoidResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                deleteVoidResult.textContent = "VOID_OK";
            })
            .deleteItemVoid(1);
    }

    @EventHandler("fullResponseBtn")
    @SuppressWarnings("unchecked")
    public void onFullResponse(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                fullResponseResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call((org.treblereel.gwt.rest.client.FullResponseCallback<Item>) (body, response) -> {
                fullResponseResult.textContent =
                    response.getStatusCode() + ":" + body.getId() + ":" + body.getName();
            })
            .getItem(1);
    }

    @EventHandler("error404Btn")
    public void onError404(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                error404Result.textContent = String.valueOf(response.getStatusCode());
            })
            .call(r -> {
                error404Result.textContent = "OK";
            })
            .getError404();
    }

    @EventHandler("error500Btn")
    public void onError500(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                error500Result.textContent = String.valueOf(response.getStatusCode());
            })
            .call(r -> {
                error500Result.textContent = "OK";
            })
            .getError500();
    }

    @EventHandler("promiseGetItemBtn")
    public void onPromiseGetItem(@ForEvent("click") MouseEvent e) {
        ((ItemService_RestCaller) itemServiceCaller).promiseGetItem(1)
            .then(item -> {
                promiseGetItemResult.textContent = item.getId() + ":" + item.getName();
            })
            .catchError(err -> {
                promiseGetItemResult.textContent = "ERROR: " + err.getMessage();
            });
    }

    @EventHandler("promiseListItemsBtn")
    @SuppressWarnings("unchecked")
    public void onPromiseListItems(@ForEvent("click") MouseEvent e) {
        ((ItemService_RestCaller) itemServiceCaller).promiseListItems()
            .then(list -> {
                promiseListItemsResult.textContent = String.valueOf(list.size());
            })
            .catchError(err -> {
                promiseListItemsResult.textContent = "ERROR: " + err.getMessage();
            });
    }

    @EventHandler("getPostBtn")
    public void onGetPost(@ForEvent("click") MouseEvent e) {
        postServiceCaller
            .onError((response, throwable) -> {
                getPostResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                Post post = (Post) r;
                getPostResult.textContent = post.getId() + ":" + post.getTitle();
            })
            .getPost(1);
    }

    @EventHandler("getPostCustomBtn")
    public void onGetPostCustom(@ForEvent("click") MouseEvent e) {
        externalApiCaller
            .onError((response, throwable) -> {
                getPostCustomResult.textContent = "ERROR: " + throwable.getMessage();
            })
            .call(r -> {
                Post post = (Post) r;
                getPostCustomResult.textContent = post.getId() + ":" + post.getTitle();
            })
            .getPost(2);
    }

    @Override
    public HTMLDivElement getElement() {
        return root;
    }
}
