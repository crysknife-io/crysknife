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
package io.crysknife.tests.templates.basic;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.navigation.client.annotation.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;

@Singleton
@Page(path = "InlinePage")
@Templated(inline = """
    <div data-field="root" id="inline-page">
      <h2 id="inline-title">Inline Template</h2>
      <p>This page uses @Templated(inline = ...) with a Java text block.</p>
      <span data-field="inlineContent" id="inline-content">Inline content here</span>
    </div>
    """)
public class InlinePage implements IsElement<HTMLDivElement> {

  @Inject
  @DataField
  HTMLDivElement root;

  @Inject
  @DataField
  @Named("span")
  HTMLElement inlineContent;

  @Override
  public HTMLDivElement getElement() {
    return root;
  }
}
