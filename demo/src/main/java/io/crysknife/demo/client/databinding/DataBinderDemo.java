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
package io.crysknife.demo.client.databinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.databinding.annotation.Bound;
import io.crysknife.ui.databinding.api.DataBinder;
import io.crysknife.ui.databinding.api.ListComponent;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page
@Templated("databinderdemo.html")
public class DataBinderDemo implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  DataBinder<Contact> binder;

  // --- Simple properties ---

  @Inject
  @DataField
  @Bound(property = "name")
  HTMLInputElement nameInput;

  @Inject
  @DataField
  @Bound(property = "email")
  HTMLInputElement emailInput;

  @Inject
  @DataField
  @Bound
  HTMLInputElement active;

  // --- Nested bean properties (dot-notation) ---

  @Inject
  @DataField
  @Bound(property = "address.city")
  HTMLInputElement cityInput;

  @Inject
  @DataField
  @Bound(property = "address.zip")
  HTMLInputElement zipInput;

  // --- Collection display ---

  @DataField
  @Bound(property = "tags")
  ListComponent<String> tagsList = ListComponent.simple("ul", "li");

  @Inject
  @DataField
  HTMLInputElement tagInput;

  @Inject
  @DataField
  HTMLButtonElement addTagBtn;

  @Inject
  @DataField
  HTMLButtonElement removeTagBtn;

  // --- Controls ---

  @Inject
  @DataField
  HTMLDivElement modelState;

  @Inject
  @DataField
  HTMLButtonElement loadBtn;

  @Inject
  @DataField
  HTMLButtonElement clearBtn;

  @PostConstruct
  public void init() {
    binder.addPropertyChangeHandler(e -> updateModelState());
  }

  @EventHandler("loadBtn")
  private void onLoad(@ForEvent("click") MouseEvent event) {
    Contact sample = new Contact();
    sample.setName("John Doe");
    sample.setEmail("john@example.com");
    sample.setActive(true);

    Address addr = new Address();
    addr.setCity("New York");
    addr.setZip("10001");
    sample.setAddress(addr);

    sample.setTags(new ArrayList<>(Arrays.asList("crysknife", "j2cl", "databinding")));

    binder.setModel(sample);
    updateModelState();
  }

  @EventHandler("clearBtn")
  private void onClear(@ForEvent("click") MouseEvent event) {
    binder.setModel(new Contact());
    updateModelState();
  }

  @EventHandler("addTagBtn")
  private void onAddTag(@ForEvent("click") MouseEvent event) {
    String tag = tagInput.value.trim();
    if (!tag.isEmpty()) {
      binder.getModel().getTags().add(tag);
      tagInput.value = "";
      updateModelState();
    }
  }

  @EventHandler("removeTagBtn")
  private void onRemoveTag(@ForEvent("click") MouseEvent event) {
    List<String> tags = binder.getModel().getTags();
    if (!tags.isEmpty()) {
      tags.remove(tags.size() - 1);
      updateModelState();
    }
  }

  private void updateModelState() {
    Contact model = binder.getModel();
    Address addr = model.getAddress();
    modelState.textContent =
        "Name: " + model.getName()
            + " | Email: " + model.getEmail()
            + " | Active: " + model.isActive()
            + " | City: " + addr.getCity()
            + ", " + addr.getZip()
            + " | Tags: " + model.getTags().toString();
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
