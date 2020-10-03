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

package io.crysknife.ui.navigation.client.local;

import elemental2.dom.HTMLElement;
import org.jboss.elemento.ElementsBag;
import io.crysknife.ui.navigation.client.local.api.DelegationControl;

import static org.jboss.elemento.Elements.removeChildrenFrom;
import static org.jboss.elemento.Elements.wrapHtmlElement;

/**
 * Default content delegation procedure.
 *
 * @author Ben Dol
 */
public class DefaultContentDelegation implements ContentDelegation {

  @Override
  public void showContent(Object page, HTMLElement container, ElementsBag elements,
      Object previousPage, DelegationControl control) {
    if (container != null && elements != null) {
      wrapHtmlElement(container).addAll(elements.elements());
    }
    control.proceed();
  }

  @Override
  public void hideContent(Object page, HTMLElement container, ElementsBag elements, Object nextPage,
      DelegationControl control) {
    removeChildrenFrom(container);
    control.proceed();
  }
}
