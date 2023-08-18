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

package io.crysknife.ui.templates.generator;

import org.jsoup.select.NodeVisitor;

public class RootNodeVisitor implements NodeVisitor {

  public org.jsoup.nodes.Element result;

  private String selector;

  public RootNodeVisitor(String selector) {
    this.selector = selector;
  }

  @Override
  public void head(org.jsoup.nodes.Node node, int i) {
    if (node.hasAttr("data-field")) {
      if (node.attr("data-field").equals(selector)) {
        result = (org.jsoup.nodes.Element) node;
      }
    } else if (node.hasAttr("id")) {
      if (node.attr("id").equals(selector)) {
        result = (org.jsoup.nodes.Element) node;
      }
    }
  }

  @Override
  public void tail(org.jsoup.nodes.Node node, int i) {

  }
}
