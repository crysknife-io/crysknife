/*
 * Copyright © 2020 Treblereel
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

import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// Sort DataField in proper order
public class DataFieldProcessor {

  private final IOCContext context;
  private final TreeLogger treeLogger;

  DataFieldProcessor(IOCContext context, TreeLogger treeLogger) {
    this.context = context;
    this.treeLogger = treeLogger;
  }

  List<DataElementInfo> process(List<DataElementInfo> dataElements, TemplateContext templateContext,
      org.jsoup.nodes.Element root) {
    LinkedList<DataElementInfo> result = new LinkedList<>(dataElements);
    Map<String, DataElementInfo> dataElementInfoMap = dataElements.stream().collect(HashMap::new,
        (m, v) -> m.put(v.getSelector(), v), HashMap::putAll);
    NodeTraversor.traverse(new NodeVisitor() {
      @Override
      public void head(Node node, int i) {
        if (node.hasAttr("data-field")) {
          String selector = node.attr("data-field");
          if (!dataElementInfoMap.containsKey(selector)) {
            treeLogger.log(TreeLogger.INFO, String.format("Unknown [data-field=%s] at %s, ignoring",
                templateContext.getDataElementType(), selector));
          } else {
            DataElementInfo temp = dataElementInfoMap.get(node.attr("data-field"));
            result.remove(temp);
            result.addLast(temp);
          }
        } else if (node.hasAttr("id") && dataElementInfoMap.containsKey(node.attr("id"))) {
          DataElementInfo temp = dataElementInfoMap.get(node.attr("id"));
          result.remove(temp);
          result.addLast(temp);
        }
      }

      @Override
      public void tail(Node node, int i) {}
    }, root);
    return result;
  }

}
