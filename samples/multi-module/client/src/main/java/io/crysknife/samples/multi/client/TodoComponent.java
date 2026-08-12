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
package io.crysknife.samples.multi.client;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.samples.multi.shared.model.Todo;
import io.crysknife.samples.multi.shared.service.TodoService;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Templated("todocomponent.html")
public class TodoComponent implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  private HTMLDivElement root;

  @Inject
  @DataField
  private HTMLInputElement todoInput;

  @Inject
  @DataField
  private HTMLButtonElement addBtn;

  @Inject
  @DataField
  private HTMLDivElement todoList;

  @Inject
  private TodoService todoService;

  @PostConstruct
  public void init() {
    todoInput.placeholder = "What needs to be done?";
  }

  @EventHandler("addBtn")
  private void onAdd(@ForEvent("click") MouseEvent event) {
    String title = todoInput.value;
    if (title != null && !title.isEmpty()) {
      todoService.addTodo(title);
      todoInput.value = "";
      renderList();
    }
  }

  private void renderList() {
    while (todoList.firstChild != null) {
      todoList.removeChild(todoList.firstChild);
    }
    int index = 0;
    for (Todo todo : todoService.getTodos()) {
      HTMLDivElement item = (HTMLDivElement) DomGlobal.document.createElement("div");
      item.textContent = todo.toString();
      item.className = todo.isCompleted() ? "todo-item todo-done" : "todo-item";
      int idx = index;
      item.addEventListener("click", e -> {
        todoService.toggleTodo(idx);
        renderList();
      });
      todoList.appendChild(item);
      index++;
    }
  }

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
