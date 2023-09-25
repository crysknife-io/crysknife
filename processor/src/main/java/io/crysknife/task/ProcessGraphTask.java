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

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/10/21
 */
public class ProcessGraphTask implements Task {

    private final MutableGraph<TypeMirror> graph =
            GraphBuilder.directed().allowsSelfLoops(false).build();
    private IOCContext context;
    private TreeLogger logger;
    private TypeElement application;

    public ProcessGraphTask(IOCContext context, TreeLogger logger, TypeElement application) {
        this.context = context;
        this.logger = logger;
        this.application = application;
    }

    @Override
    public void execute() throws UnableToCompleteException {
        Set<TypeMirror> state = new HashSet<>();
        Stack<TypeMirror> stack = new Stack<>();
        stack.push(application.asType());
        while (!stack.isEmpty()) {
            TypeMirror scan = stack.pop();
            BeanDefinition parent = context.getBeans().get(scan);
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

        context.getBeans().forEach((bean, definition) -> {
            if (!context.getOrderedBeans().contains(bean)) {
                context.getOrderedBeans().add(bean);
            }
        });
    }
}
