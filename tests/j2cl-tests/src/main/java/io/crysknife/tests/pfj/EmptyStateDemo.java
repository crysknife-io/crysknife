/*
 * Copyright © 2023 Treblereel
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

package io.crysknife.tests.pfj;

import jakarta.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.patternfly.component.emptystate.EmptyState;

import static org.patternfly.component.button.Button.button;
import static org.patternfly.component.emptystate.EmptyState.emptyState;
import static org.patternfly.component.emptystate.EmptyStateActions.emptyStateActions;
import static org.patternfly.component.emptystate.EmptyStateBody.emptyStateBody;
import static org.patternfly.component.emptystate.EmptyStateFooter.emptyStateFooter;
import static org.patternfly.icon.IconSets.fas.cubes;
import static org.patternfly.style.Size.lg;
import static org.patternfly.style.Size.sm;
import static org.patternfly.style.Size.xl;

@Page(path = "EmptyStateDemo")
@Singleton
@Templated("EmptyStateDemo.html")
public class EmptyStateDemo implements IsElement<HTMLDivElement> {

  @DataField
  EmptyState basicEmpty = emptyState()
      .text("Empty state")
      .icon(cubes())
      .addBody(emptyStateBody().text(
          "This represents the empty state pattern in PatternFly. Hopefully it's simple enough to use but flexible enough to meet a variety of needs."))
      .addFooter(emptyStateFooter()
          .addActions(emptyStateActions()
              .add(button("Primary action").primary()))
          .addActions(emptyStateActions()
              .add(button("Multiple").link())
              .add(button("Action Buttons").link())
              .add(button("Can go here").link())));

  @DataField
  EmptyState smallEmpty = emptyState()
      .size(sm)
      .text("Small empty state")
      .icon(cubes())
      .addBody(emptyStateBody().text("A smaller variant for tighter spaces."))
      .addFooter(emptyStateFooter()
          .addActions(emptyStateActions()
              .add(button("Primary action").primary())));

  @DataField
  EmptyState largeEmpty = emptyState()
      .size(xl)
      .text("Extra large empty state")
      .icon(cubes())
      .addBody(emptyStateBody().text("An extra large variant for prominent empty states."))
      .addFooter(emptyStateFooter()
          .addActions(emptyStateActions()
              .add(button("Primary action").primary())));

  @DataField
  EmptyState spinnerEmpty = emptyState()
      .text("Loading...")
      .spinner("Loading data...");
}
