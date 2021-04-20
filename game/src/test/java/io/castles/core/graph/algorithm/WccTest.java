package io.castles.core.graph.algorithm;

import io.castles.core.graph.Graph;
import io.castles.core.tile.TileContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WccTest {

    Graph graph;

    @Nested
    class SingleComponent {

        Graph.Node n1;
        Graph.Node n2;
        Graph.Node n3;
        Graph.Node n4;
        Graph.Node n5;

        /**
         * (n1) (n2)
         *   \   /
         *    (n3)
         *   /   \
         * (n4)  (n5)
         */
        @BeforeEach
        void setup() {
            graph = new Graph(TileContent.GRAS);

            n1 = new Graph.Node(0, 0, 0, 0);
            n2 = new Graph.Node(1, 0, 1, 1);
            n3 = new Graph.Node(2, 0, 2, 2);
            n4 = new Graph.Node(3, 0, 3, 3);
            n5 = new Graph.Node(4, 0, 4, 4);

            graph.addRelationship(n1, n3);
            graph.addRelationship(n2, n3);
            graph.addRelationship(n3, n4);
            graph.addRelationship(n3, n5);
        }

        @Test
        void shouldComputeCorrectComponents() {
            var wcc = new Wcc(graph);
            wcc.compute();

            assertThat(wcc.sameComponent(n1, n2)).isTrue();
            assertThat(wcc.sameComponent(n1, n3)).isTrue();
            assertThat(wcc.sameComponent(n1, n4)).isTrue();
            assertThat(wcc.sameComponent(n1, n5)).isTrue();
        }

    }

    @Nested
    class MultipleComponents {

        Graph.Node n1;
        Graph.Node n2;
        Graph.Node n3;
        Graph.Node n4;
        Graph.Node n5;

        /**
         * (n1)--(n2)
         *    (n3)
         * (n4)--(n5)
         */
        @BeforeEach
        void setup() {
            graph = new Graph(TileContent.GRAS);

            n1 = new Graph.Node(0, 0, 0, 0);
            n2 = new Graph.Node(1, 0, 1, 1);
            n3 = new Graph.Node(2, 0, 2, 2);
            n4 = new Graph.Node(3, 0, 3, 3);
            n5 = new Graph.Node(4, 0, 4, 4);

            graph.addNode(n3);
            graph.addRelationship(n1, n2);
            graph.addRelationship(n4, n5);
        }

        @Test
        void shouldComputeCorrectComponents() {
            var wcc = new Wcc(graph);
            wcc.compute();

            assertThat(wcc.sameComponent(n1, n2)).isTrue();
            assertThat(wcc.sameComponent(n1, n3)).isFalse();
            assertThat(wcc.sameComponent(n3, n4)).isFalse();
            assertThat(wcc.sameComponent(n4, n5)).isTrue();
        }

    }
}