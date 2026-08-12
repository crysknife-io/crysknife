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
package io.crysknife.samples.multi.client.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Singleton;

import io.crysknife.samples.multi.shared.model.Todo;
import io.crysknife.samples.multi.shared.service.TodoService;

@Singleton
public class TodoServiceImpl implements TodoService {

  private final List<Todo> todos = new ArrayList<>();

  @Override
  public void addTodo(String title) {
    todos.add(new Todo(title));
  }

  @Override
  public List<Todo> getTodos() {
    return todos;
  }

  @Override
  public void toggleTodo(int index) {
    if (index >= 0 && index < todos.size()) {
      Todo todo = todos.get(index);
      todo.setCompleted(!todo.isCompleted());
    }
  }
}
