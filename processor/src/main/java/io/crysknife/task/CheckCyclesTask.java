/*
 * Copyright © 2021 Treblereel
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

package io.crysknife.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.lang.model.type.TypeMirror;

import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/16/21
 */
public class CheckCyclesTask implements Task {

  private final IOCContext context;
  private final TreeLogger logger;

  public CheckCyclesTask(IOCContext iocContext, TreeLogger logger) {
    this.context = iocContext;
    this.logger = logger;
  }

  @Override
  public void execute() throws UnableToCompleteException {
    Map<TypeMirror, Vertex> vertexes = new HashMap<>();
    Graph graph = new Graph();

    for (Map.Entry<TypeMirror, BeanDefinition> entry : context.getBeans().entrySet()) {
      Vertex vertex = new Vertex(entry.getValue());
      graph.addVertex(vertex);
      vertexes.put(entry.getKey(), vertex);
    }

    for (Map.Entry<TypeMirror, BeanDefinition> entry : context.getBeans().entrySet()) {
      entry.getValue().getDependencies().forEach(dep -> {
        Vertex from = vertexes.get(entry.getKey());
        Vertex to = vertexes.get(dep.getType());
        // check, if unscoped bean
        if (to != null) {
          graph.addEdge(from, to);
        }
      });
    }

    if (graph.hasCycle()) {
      System.out.println("Graph contains cyclic deps ["
          + graph.pair.get().getKey().beanDefinition.getQualifiedName() + " <-> "
          + graph.pair.get().getValue().beanDefinition.getQualifiedName() + "]");
    }

  }


  private static class Graph {

    public Optional<Pair<Vertex, Vertex>> pair = Optional.empty();
    private List<Vertex> vertices;

    public Graph() {
      this.vertices = new ArrayList<>();
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
          pair = Optional.of(new ImmutablePair<>(sourceVertex, neighbour));
          return true;
        } else if (!neighbour.isVisited() && hasCycle(neighbour)) {
          return true;
        }
      }
      sourceVertex.setBeingVisited(false);
      sourceVertex.setVisited(true);
      return false;
    }

  }

  private static class Vertex {

    private BeanDefinition beanDefinition;

    private boolean visited;

    private boolean beingVisited;

    private List<Vertex> adjacencyList;

    public Vertex(BeanDefinition beanDefinition) {
      this.beanDefinition = beanDefinition;
      this.adjacencyList = new ArrayList<>();
    }

    public BeanDefinition get() {
      return beanDefinition;
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

    public void addNeighbour(Vertex adjacent) {
      this.adjacencyList.add(adjacent);
    }

  }

}
