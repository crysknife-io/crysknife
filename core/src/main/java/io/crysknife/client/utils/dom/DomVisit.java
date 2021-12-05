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

package io.crysknife.client.utils.dom;

import elemental2.dom.Element;
import elemental2.dom.Node;
import elemental2.dom.NodeList;

/**
 * @author edewit@redhat.com
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class DomVisit {

  /**
   * Called to traverse and visit the tree of {@link Element}s.
   * 
   * @param element the root of the tree to traverse and visit
   * @param visitor the visitor to be called on each of the nodes.
   */
  public static void visit(Element element, DomVisitor visitor) {
    if (!visitor.visit(element))
      return;
    NodeList childNodes = element.childNodes;
    for (int idx = 0; idx < childNodes.getLength(); idx++) {
      Node childNode = (Node) childNodes.item(idx);
      if (childNode.nodeType == Node.ELEMENT_NODE) {
        visit((Element) childNode, visitor);
      }
    }
  }

  public static void revisit(Element element, DomRevisitor visitor) {
    if (visitor.visit(element)) {
      NodeList childNodes = element.childNodes;
      for (int idx = 0; idx < childNodes.getLength(); idx++) {
        Node childNode = (Node) childNodes.item(idx);
        if (childNode.nodeType == Node.ELEMENT_NODE) {
          revisit((Element) childNode, visitor);
        }
      }
    }
    visitor.afterVisit(element);
  }

}
