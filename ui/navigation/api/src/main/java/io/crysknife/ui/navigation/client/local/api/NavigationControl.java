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

package io.crysknife.ui.navigation.client.local.api;

import io.crysknife.client.internal.collections.ImmutableMultimap;
import io.crysknife.client.internal.collections.Multimap;
import io.crysknife.ui.navigation.client.local.Navigation;
import io.crysknife.ui.navigation.client.local.Page;
import io.crysknife.ui.navigation.client.local.PageHiding;

/**
 * Instances of this class are passed to {@link PageHiding} methods. If the parameter is present,
 * the page navigation will not be carried out until {@link NavigationControl#proceed()} is invoked.
 * This is useful for interrupting page navigations and then resuming at a later time (for example,
 * to prompt the user to save their work before transitioning to a new page).
 */
public class NavigationControl {

  private final Navigation navigation;
  private final Runnable runnable;
  private Runnable interrupt;

  private boolean hasRun;

  public NavigationControl(Navigation navigation, Runnable runnable) {
    this.navigation = navigation;
    this.runnable = runnable;
  }

  public NavigationControl(Navigation navigation, Runnable runnable, Runnable interrupt) {
    this(navigation, runnable);
    this.interrupt = interrupt;
  }

  /** Causes page navigation to proceed. */
  public void proceed() {
    if (!hasRun) {
      runnable.run();
      hasRun = true;
    } else {
      throw new IllegalStateException("proceed() method can only be called once.");
    }
  }

  /** Interrupt the navigation process. */
  public void interrupt() {
    if (!hasRun && interrupt != null) {
      interrupt.run();
      hasRun = true;
    }
  }

  /**
   * Redirect to a given page safely.
   *
   * @param toPage Page class annotated with {@link Page}.
   */
  public <C> void redirect(Class<C> toPage) {
    redirect(toPage, ImmutableMultimap.of());
  }

  /**
   * Redirect to a given page safely.
   *
   * @param toPage page class annotated with {@link Page}.
   * @param state Pages state map.
   */
  public <C> void redirect(Class<C> toPage, Multimap<String, String> state) {
    if (!hasRun) {
      interrupt();
      navigation.goTo(toPage, state);
    } else {
      throw new IllegalStateException("redirect() method can only be called once.");
    }
  }

  public boolean hasRun() {
    return hasRun;
  }
}
