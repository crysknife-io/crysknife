/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

import elemental2.core.JsArray;
import elemental2.dom.HTMLElement;
import io.crysknife.ui.navigation.client.internal.DelegationControl;

/**
 * Content delegation control interface.
 *
 * @author Ben Dol
 */
public interface ContentDelegation {

  /**
   * Called when the page is showing its content (setting the navigation container).
   *
   * @param page the current page being shown.
   * @param container the navigation container.
   * @param elements the element(s) of the current page.
   * @param previousPage the previous page, <b>this can be null</b>.
   * @param control the delegation control for proceeding navigation process.
   */
  void showContent(Object page, HTMLElement container, JsArray<HTMLElement> elements,
      Object previousPage, DelegationControl control);

  /**
   * Called when the page is hiding its content (clearing the navigation container).
   *
   * @param page the current page being hidden.
   * @param container the navigation container.
   * @param elements the element(s) of the current page.
   * @param nextPage potential next requested page, <b>this can be null</b>.
   * @param control the delegation control for proceeding navigation process.
   */
  void hideContent(Object page, HTMLElement container, JsArray<HTMLElement> elements,
      Object nextPage, DelegationControl control);
}
