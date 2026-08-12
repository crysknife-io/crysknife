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
import org.patternfly.component.content.Content;

import static org.patternfly.component.content.Content.content;
import static org.patternfly.component.content.ContentType.h1;
import static org.patternfly.component.content.ContentType.h2;
import static org.patternfly.component.content.ContentType.h3;
import static org.patternfly.component.content.ContentType.h4;
import static org.patternfly.component.content.ContentType.p;

@Page(path = "ContentDemo")
@Singleton
@Templated("ContentDemo.html")
public class ContentDemo implements IsElement<HTMLDivElement> {

  @DataField
  Content headings = content()
      .add(content(h1).text("Hello World"))
      .add(content(h2).text("Second Level"))
      .add(content(h3).text("Third Level"))
      .add(content(h4).text("Fourth Level"));

  @DataField
  Content bodyContent = content()
      .add(content(p).text(
          "Content components provide default styling for common HTML elements such as headings, paragraphs, and lists. "
              + "They can be used as wrappers to style children elements or as standalone components."))
      .add(content(p).text(
          "Using the Content component ensures consistent typography across your application and "
              + "aligns with PatternFly design guidelines."));

  @DataField
  Content editorialContent = content().editorial()
      .add(content(h1).text("Editorial content example"))
      .add(content(p).text(
          "Editorial content provides a more readable, document-like experience. It adjusts font "
              + "sizes and line heights to improve readability for longer-form content."));
}
