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

package org.treblereel.gwt.crysknife.client.internal;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 2/21/19
 */
public class GraphTest {

  @Test
  public void testGraph() {
    MutableGraph<String> graph = GraphBuilder.directed().build();
    graph.addNode("A");
    graph.addNode("B");
    graph.addNode("C");
    graph.addNode("D");
    graph.addNode("E");
    graph.addNode("F");

    graph.putEdge("A", "B");
    graph.putEdge("B", "C");
    graph.putEdge("B", "D");
    graph.putEdge("A", "D");
    graph.putEdge("D", "E");
    graph.putEdge("E", "F");

    Traverser.forGraph(graph).depthFirstPostOrder("A").forEach(n -> {
      System.out.println(n);
    });

    assertTrue(true);
  }
}
