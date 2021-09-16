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

package io.crysknife.client.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GraphCycleTest {

  @Test
  public void test() {

    Vertex vertex0 = new Vertex("0");
    Vertex vertexA = new Vertex("A");
    Vertex vertexB = new Vertex("B");
    Vertex vertexC = new Vertex("C");
    Vertex vertexD = new Vertex("D");
    Vertex vertexE = new Vertex("E");
    Vertex vertexF = new Vertex("F");
    Vertex vertexG = new Vertex("G");

    Graph graph = new Graph();
    graph.addVertex(vertex0);
    graph.addVertex(vertexA);
    graph.addVertex(vertexB);
    graph.addVertex(vertexC);
    graph.addVertex(vertexD);

    graph.addEdge(vertexA, vertexB);
    graph.addEdge(vertexA, vertexG);
    graph.addEdge(vertexB, vertexC);
    graph.addEdge(vertexC, vertexD);
    graph.addEdge(vertexD, vertexE);
    graph.addEdge(vertexE, vertexA);
    graph.addEdge(vertexE, vertexF);


    graph.addEdge(vertexE, vertexF);
    graph.addEdge(vertexF, vertexG);
    graph.addEdge(vertexG, vertexE);
    graph.addEdge(vertexG, vertex0);

    assertTrue(graph.hasCycle());
    assertEquals(vertexE, graph.pair.get().a);
    assertEquals(vertexA, graph.pair.get().b);
  }

  @Test
  public void negative() {

    Vertex vertexA = new Vertex("A");
    Vertex vertexB = new Vertex("B");
    Vertex vertexC = new Vertex("C");
    Vertex vertexD = new Vertex("D");

    Graph graph = new Graph();
    graph.addVertex(vertexA);
    graph.addVertex(vertexB);
    graph.addVertex(vertexC);
    graph.addVertex(vertexD);

    graph.addEdge(vertexA, vertexB);
    graph.addEdge(vertexB, vertexC);
    graph.addEdge(vertexA, vertexC);
    graph.addEdge(vertexD, vertexC);

    assertFalse(graph.hasCycle());
  }
}
