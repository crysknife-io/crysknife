package org.treblereel.gwt.crysknife.client.internal;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/21/19
 */
public class GraphTest {



    @Test
    public void testGraph(){
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        graph.addNode("E");
        graph.addNode("F");

        graph.putEdge("A","B");
        graph.putEdge("B","C");
        graph.putEdge("B","D");
        graph.putEdge("A","D");
        graph.putEdge("D","E");
        graph.putEdge("E","F");


        Traverser.forGraph(graph).depthFirstPostOrder("A").forEach(n ->{
            System.out.println(n);
        });


        assertTrue(true);
    }
}
