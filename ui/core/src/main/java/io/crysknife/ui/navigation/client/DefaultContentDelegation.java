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
 * Default content delegation procedure.
 *
 * @author Ben Dol
 */
public class DefaultContentDelegation implements ContentDelegation {

  @Override
  public void showContent(Object page, HTMLElement container, JsArray<HTMLElement> elements,
      Object previousPage, DelegationControl control) {
    if (container != null && elements != null) {
      elements.forEach((node, index) -> {
        container.append(node);
        return null;
      });
    }
    control.proceed();
  }

  @Override
  public void hideContent(Object page, HTMLElement container, JsArray<HTMLElement> elements,
      Object nextPage, DelegationControl control) {
    if (container != null) {
      while (container.firstChild != null) {
        container.removeChild(container.firstChild);
      }
    }
    control.proceed();
  }
}
