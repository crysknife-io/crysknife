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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Graph {

  private List<Vertex> vertices;

  public Optional<Pair<Vertex>> pair = Optional.empty();

  public Graph() {
    this.vertices = new ArrayList<>();
  }

  public Graph(List<Vertex> vertices) {
    this.vertices = vertices;
  }

  public void addVertex(Vertex vertex) {
    this.vertices.add(vertex);
  }

  public void addEdge(Vertex from, Vertex to) {
    from.addNeighbour(to);
  }

  public boolean hasCycle() {
    for (Vertex vertex : vertices) {
      if (!vertex.isVisited() && hasCycle(vertex)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasCycle(Vertex sourceVertex) {
    sourceVertex.setBeingVisited(true);

    for (Vertex neighbour : sourceVertex.getAdjacencyList()) {
      if (neighbour.isBeingVisited()) {
        pair = Optional.of(new Pair<>(sourceVertex, neighbour));
        // backward edge exists
        return true;
      } else if (!neighbour.isVisited() && hasCycle(neighbour)) {
        return true;
      }
    }

    sourceVertex.setBeingVisited(false);
    sourceVertex.setVisited(true);
    return false;
  }

  public static class Pair<T> {
    T a;
    T b;

    Pair(T a, T b) {
      this.a = a;
      this.b = b;
    }

  }

}
