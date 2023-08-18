/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package io.crysknife.ui.navigation.client.internal;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLAnchorElement;
import io.crysknife.client.internal.Assert;
import io.crysknife.client.internal.collections.ImmutableMultimap;
import io.crysknife.client.internal.collections.Multimap;
import io.crysknife.ui.navigation.client.HistoryToken;
import io.crysknife.ui.navigation.client.HistoryTokenFactory;
import io.crysknife.ui.navigation.client.Navigation;
import io.crysknife.ui.navigation.client.internal.PageNode;

/**
 * Represents an anchor widget that, when clicked, will navigate the user to another page in the
 * application's flow.
 * <p>
 * Instances of this class are normally obtained via dependency injection.
 * <p>
 * Instances of this class are immutable.
 *
 * @param <P> The type of the target page ("to page")
 * @author eric.wittmann@redhat.com
 */
public final class TransitionAnchor<P> implements EventListener {

  private final Navigation navigation;
  private final Class<P> toPageWidgetType;
  private final Multimap<String, String> state;
  private final HistoryTokenFactory htFactory;
  private final HTMLAnchorElement anchor =
      (HTMLAnchorElement) DomGlobal.document.createElement("a");

  /**
   * Creates a new TransitionAnchor with the given attributes.
   *
   * @param navigation The navigation system this page transition participates in.
   * @param toPage The page type this transition goes to. Not null.
   * @throws NullPointerException if any of the arguments are null.
   */
  TransitionAnchor(Navigation navigation, final Class<P> toPage, HistoryTokenFactory htFactory) {
    this(navigation, toPage, ImmutableMultimap.of(), htFactory);
  }

  /**
   * Creates a new TransitionAnchor with the given attributes.
   *
   * @param navigation The navigation system this page transition participates in.
   * @param toPage The page type this transition goes to. Not null.
   * @param state The page state. Cannot be null (but can be an empty multimap)
   * @throws NullPointerException if any of the arguments are null.
   */
  TransitionAnchor(Navigation navigation, final Class<P> toPage,
      final Multimap<String, String> state, HistoryTokenFactory htFactory) {
    this.navigation = Assert.notNull(navigation);
    this.toPageWidgetType = Assert.notNull(toPage);
    this.state = Assert.notNull(state);
    this.htFactory = Assert.notNull(htFactory);
    this.anchor.addEventListener("click", this);

    this.initHref(toPage, state);
  }

  /**
   * Initialize the anchor's href attribute.
   *
   * @param toPage The page type this transition goes to. Not null.
   * @param state The page state. Cannot be null (but can be an empty multimap)
   */
  private void initHref(Class<P> toPage, Multimap<String, String> state) {
    PageNode<P> toPageInstance = navigation.getNavGraph().getPage(toPage);
    HistoryToken token = htFactory.createHistoryToken(toPageInstance.name(), state);
    String href = "#" + token.toString();
    anchor.href = href;
  }

  /**
   * The page this transition goes to.
   */
  public Class<P> toPageType() {
    return toPageWidgetType;
  }

  /**
   * Programmatically click on the anchor.
   */
  public void click() {
    navigation.goTo(toPageWidgetType, this.state);
  }

  /**
   * Programmatically click on the anchor (with alternate page state).
   *
   * @param state
   */
  public void click(Multimap<String, String> state) {
    navigation.goTo(toPageWidgetType, state);
  }

  @Override
  public void handleEvent(Event evt) {
    navigation.goTo(toPageWidgetType, this.state);
    evt.stopPropagation();
    evt.preventDefault();
  }
}
