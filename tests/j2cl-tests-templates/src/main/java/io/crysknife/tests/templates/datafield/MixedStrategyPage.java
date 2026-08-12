/*
 * Copyright © 2026 Treblereel
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
package io.crysknife.tests.templates.datafield;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.ConflictStrategy;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(path = "MixedStrategy")
@Templated
public class MixedStrategyPage implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @DataField(strategy = ConflictStrategy.USE_BEAN)
  HTMLDivElement beanEmpty = createEmptyDiv();

  @DataField(strategy = ConflictStrategy.USE_TEMPLATE)
  HTMLDivElement templateWins = createDivWithId();

  @Override
  public HTMLDivElement getElement() {
    return root;
  }

  private static HTMLDivElement createEmptyDiv() {
    HTMLDivElement div = (HTMLDivElement) DomGlobal.document.createElement("div");
    div.id = "bean-empty-div";
    return div;
  }

  private static HTMLDivElement createDivWithId() {
    HTMLDivElement div = (HTMLDivElement) DomGlobal.document.createElement("div");
    div.id = "bean-template-wins";
    div.textContent = "Bean text";
    return div;
  }
}
