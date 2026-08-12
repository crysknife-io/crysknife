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
package io.crysknife.ui.databinding.api;

import java.util.List;
import java.util.function.BiConsumer;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;

/**
 * A container component that automatically renders list items as child DOM elements.
 *
 * <p>Usage with {@code @Bound}:
 * <pre>{@code
 * @DataField
 * @Bound(property = "tags")
 * ListComponent<String> tagsList = ListComponent.simple("ul", "li");
 * }</pre>
 *
 * <p>With custom renderer:
 * <pre>{@code
 * @DataField
 * @Bound(property = "tags")
 * ListComponent<String> tagsList = ListComponent.create("ul", "li",
 *     (tag, el) -> {
 *         el.textContent = tag;
 *         el.classList.add("badge");
 *     });
 * }</pre>
 *
 * @param <M> the list element type
 * @author Dmitrii Tikhomirov
 */
public class ListComponent<M> implements IsElement<HTMLElement> {

  private final HTMLElement container;
  private final String itemTag;
  private BiConsumer<M, HTMLElement> renderer;

  private ListComponent(String containerTag, String itemTag, BiConsumer<M, HTMLElement> renderer) {
    this.container = (HTMLElement) DomGlobal.document.createElement(containerTag);
    this.itemTag = itemTag;
    this.renderer = renderer;
  }

  /**
   * Creates a ListComponent with the default renderer that sets {@code textContent}
   * to {@code item.toString()}.
   *
   * @param containerTag the container element tag (e.g. "ul", "ol", "div")
   * @param itemTag the tag for each item element (e.g. "li", "div")
   */
  public static <M> ListComponent<M> simple(String containerTag, String itemTag) {
    return new ListComponent<>(containerTag, itemTag,
        (item, el) -> el.textContent = String.valueOf(item));
  }

  /**
   * Creates a ListComponent with a custom renderer.
   *
   * @param containerTag the container element tag
   * @param itemTag the tag for each item element
   * @param renderer a function that populates each item element from the model value
   */
  public static <M> ListComponent<M> create(String containerTag, String itemTag,
      BiConsumer<M, HTMLElement> renderer) {
    return new ListComponent<>(containerTag, itemTag, renderer);
  }

  /**
   * Replaces all items in the container. Called by the binding agent when
   * the list property changes or the observable list is mutated.
   */
  @SuppressWarnings("unchecked")
  public void setItems(List<M> items) {
    while (container.firstChild != null) {
      container.removeChild(container.firstChild);
    }
    if (items != null) {
      for (M item : items) {
        HTMLElement el = (HTMLElement) DomGlobal.document.createElement(itemTag);
        renderer.accept(item, el);
        container.appendChild(el);
      }
    }
  }

  @Override
  public HTMLElement getElement() {
    return container;
  }
}
