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

package io.crysknife.ui.navigation.client;

import io.crysknife.client.internal.Assert;
import io.crysknife.client.internal.collections.ImmutableMultimap;
import io.crysknife.client.internal.collections.Multimap;
import io.crysknife.ui.navigation.client.internal.PageNode;

/**
 * Represents navigability from one page to another in the application's flow. Thinking of the
 * application flow as a directed graph, {@link PageNode PageNodes} are the nodes and
 * PageTransitions are the edges.
 * <p>
 * Instances of this class are normally obtained via dependency injection.
 * <p>
 * Instances of this class are immutable.
 *
 * @param <P> The type of the target page ("to page")
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
public final class TransitionTo<P> {

  private final Class<P> toPageWidgetType;

  private Navigation navigation;

  /**
   * Creates a new PageTransition with the given attributes.
   *
   * @param navigation The navigation system this page transition participates in.
   * @param toPage The page type this transition goes to. Not null.
   *
   * @throws NullPointerException if any of the arguments are null.
   */
  public TransitionTo(Class<P> toPage, Navigation navigation) {
    this.toPageWidgetType = Assert.notNull(toPage);
    this.navigation = navigation;
  }

  /**
   * The page this transition goes to.
   */
  public Class<P> toPageType() {
    return toPageWidgetType;
  }

  /**
   * Transitions the application's view from the current page (whatever it is) to the {@code toPage}
   * of this transition, passing no extra state information.
   * <p>
   * Note: if the Navigation framework is being used together with ErraiIOC in asynchronous mode,
   * the page transition may not have happened by the time this method returns.
   */
  public void go() {
    go(ImmutableMultimap.of());
  }

  /**
   * Transitions the application's view from the current page (whatever it is) to the {@code toPage}
   * of this transition, passing the given extra state information.
   * <p>
   * Note: if the Navigation framework is being used together with ErraiIOC in asynchronous mode,
   * the page transition may not have happened by the time this method returns.
   *
   * @param state Extra state information that should be passed to the page before it is displayed.
   *        Must not be null.
   */
  public void go(final Multimap<String, String> state) {
    navigation.goTo(toPageWidgetType, state);
  }
}
