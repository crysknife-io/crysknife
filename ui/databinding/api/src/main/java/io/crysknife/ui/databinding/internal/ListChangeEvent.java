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
package io.crysknife.ui.databinding.internal;

import java.util.List;

/**
 * Event describing a mutation in an {@link ObservableList} or {@link ObservableSet}.
 *
 * @param <T> element type
 * @author Dmitrii Tikhomirov
 */
public class ListChangeEvent<T> {

  public enum ChangeType {
    ADD, REMOVE, SET, CLEAR
  }

  private final ChangeType type;
  private final int index;
  private final List<T> addedElements;
  private final List<T> removedElements;

  public ListChangeEvent(ChangeType type, int index, List<T> addedElements,
      List<T> removedElements) {
    this.type = type;
    this.index = index;
    this.addedElements = addedElements;
    this.removedElements = removedElements;
  }

  public ChangeType getType() {
    return type;
  }

  public int getIndex() {
    return index;
  }

  public List<T> getAddedElements() {
    return addedElements;
  }

  public List<T> getRemovedElements() {
    return removedElements;
  }
}
