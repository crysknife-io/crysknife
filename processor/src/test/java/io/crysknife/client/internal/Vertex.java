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

public class Vertex {

  private String label;

  private boolean visited;

  private boolean beingVisited;

  private List<Vertex> adjacencyList;

  public Vertex(String label) {
    this.label = label;
    this.adjacencyList = new ArrayList<>();
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isVisited() {
    return visited;
  }

  public void setVisited(boolean visited) {
    this.visited = visited;
  }

  public boolean isBeingVisited() {
    return beingVisited;
  }

  public void setBeingVisited(boolean beingVisited) {
    this.beingVisited = beingVisited;
  }

  public List<Vertex> getAdjacencyList() {
    return adjacencyList;
  }

  public void setAdjacencyList(List<Vertex> adjacencyList) {
    this.adjacencyList = adjacencyList;
  }

  public void addNeighbour(Vertex adjacent) {
    this.adjacencyList.add(adjacent);
  }
}
