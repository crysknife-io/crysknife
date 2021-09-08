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

package io.crysknife.generator.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.nextstep.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/5/19
 */
public class Graph {

  private final Map<TypeMirror, BeanDefinition> beans;
  private final IOCContext context;

  private final MutableGraph<TypeMirror> graph =
      GraphBuilder.directed().allowsSelfLoops(false).build();

  public Graph(IOCContext context, Map<TypeMirror, BeanDefinition> beans) {
    this.context = context;
    this.beans = beans;
  }

  public void process(TypeElement application) {
    Set<TypeMirror> state = new HashSet<>();
    Stack<TypeMirror> stack = new Stack<>();
    stack.push(application.asType());
    while (!stack.isEmpty()) {
      TypeMirror scan = stack.pop();
      BeanDefinition parent = beans.get(scan);
      graph.addNode(scan);
      if (parent == null) {
        continue;
      }
      parent.getDependencies().forEach(deps -> {
        if (!deps.getType().equals(scan)) {
          graph.putEdge(scan, deps.getType());
        }

        if (!state.contains(deps.getType())) {
          stack.push(deps.getType());
          state.add(deps.getType());
        }
      });
    }

    Traverser.forGraph(graph).depthFirstPostOrder(application.asType())
        .forEach(bean -> context.getOrderedBeans().add(bean));

    beans.forEach((bean, definition) -> {
      if (!context.getOrderedBeans().contains(bean)) {
        context.getOrderedBeans().add(bean);
      }
    });
  }
}
