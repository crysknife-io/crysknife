/*
 * Copyright (C) 2014 Google, Inc.
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

package org.jboss.gwt.elemento.processor;

public class TemplateSelector {

  public final String template;
  public final String selector;
  public final boolean inline;

  public TemplateSelector(final String template) {
    this(template, null, false);
  }

  public TemplateSelector(final String template, final String selector) {
    this(template, selector, false);
  }

  public TemplateSelector(final String template, final String selector, final boolean inline) {
    this.template = template;
    this.selector = selector;
    this.inline = inline;
  }

  public static TemplateSelector ofInline(final String html) {
    return new TemplateSelector(html, null, true);
  }

  public boolean hasSelector() {
    return selector != null;
  }

  @Override
  public String toString() {
    if (inline) {
      return "<inline template>";
    }
    return template + (hasSelector() ? "#" + selector : "");
  }
}
