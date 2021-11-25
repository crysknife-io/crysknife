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

import com.google.auto.common.MoreTypes;
import io.crysknife.generator.context.IOCContext;
import org.jboss.gwt.elemento.processor.context.DataElementInfo;
import org.jboss.gwt.elemento.processor.context.TemplateContext;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// Sort DataField in proper order
public class DataFieldProcessor {

  private final IOCContext context;

  DataFieldProcessor(IOCContext context) {
    this.context = context;
  }

  List<DataElementInfo> process(List<DataElementInfo> dataElements, TemplateContext templateContext,
      org.jsoup.nodes.Element root) {
    LinkedList<DataElementInfo> result = new LinkedList<>();
    result.addAll(dataElements);
    Map<String, DataElementInfo> dataElementInfoMap = new HashMap<>();

    dataElements.forEach(de -> {
      dataElementInfoMap.put(de.getSelector(), de);
    });

    org.jsoup.select.NodeTraversor.traverse(new NodeVisitor() {
      @Override
      public void head(Node node, int i) {
        if (node.hasAttr("data-field")) {
          String selector = node.attr("data-field");
          if (!dataElementInfoMap.containsKey(selector)) {
            context.getGenerationContext().getProcessingEnvironment().getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                String.format("Unknown [data-field=%s] at %s, ignoring", selector,
                    templateContext.getDataElementType()),
                MoreTypes.asTypeElement(templateContext.getDataElementType()));
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
