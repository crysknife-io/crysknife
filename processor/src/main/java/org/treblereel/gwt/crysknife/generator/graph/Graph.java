package org.treblereel.gwt.crysknife.generator.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.lang.model.element.TypeElement;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/5/19
 */
public class Graph {

    private final IOCContext context;

    private final MutableGraph<TypeElement> graph = GraphBuilder.directed().allowsSelfLoops(false).build();

    public Graph(IOCContext context) {
        this.context = context;
    }

    public void process(TypeElement application) {
        Set<TypeElement> state = new HashSet<>();
        Stack<TypeElement> stack = new Stack<>();
        stack.push(application);
        while (!stack.isEmpty()) {
            TypeElement scan = stack.pop();
            BeanDefinition parent = context.getBeans().get(scan);
            graph.addNode(scan);
            if (parent == null) {
                continue;
            }
            parent.getDependsOn().forEach(deps -> {
                if (!deps.getType()
                        .equals(scan)) {
                    graph.putEdge(scan, deps.getType());
                }

                if (!state.contains(deps.getType())) {
                    stack.push(deps.getType());
                    state.add(deps.getType());
                }
            });
        }

        Traverser.forGraph(graph)
                .depthFirstPostOrder(application)
                .forEach(bean -> context.getOrderedBeans().add(bean));

        context.getBeans().forEach((bean, definition) -> {
            if (!context.getOrderedBeans().contains(bean)) {
                context.getOrderedBeans().add(bean);
            }
        });
    }
}
