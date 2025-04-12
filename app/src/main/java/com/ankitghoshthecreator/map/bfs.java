package com.ankitghoshthecreator.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class bfs {

    public static Pair<List<Node>, Long> findPath(List<Node> nodes, List<Edge> edges, String startId, String endId) {
        long startTime = System.currentTimeMillis();

        // Create adjacency list from edges
        Map<String, List<String>> adjacencyMap = new HashMap<>();
        for (Edge edge : edges) {
            if (!adjacencyMap.containsKey(edge.getFrom())) {
                adjacencyMap.put(edge.getFrom(), new ArrayList<>());
            }
            adjacencyMap.get(edge.getFrom()).add(edge.getTo());

            // For undirected graph (can go both ways)
            if (!adjacencyMap.containsKey(edge.getTo())) {
                adjacencyMap.put(edge.getTo(), new ArrayList<>());
            }
            adjacencyMap.get(edge.getTo()).add(edge.getFrom());
        }

        // BFS implementation
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();

        queue.add(startId);
        visited.add(startId);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(endId)) break;

            if (adjacencyMap.containsKey(current)) {
                for (String neighbor : adjacencyMap.get(current)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        parentMap.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();
        String current = endId;

        while (!current.equals(startId)) {
            path.add(0, current);
            if (!parentMap.containsKey(current)) break;
            current = parentMap.get(current);
        }

        path.add(0, startId);

        // Convert to Node objects
        List<Node> nodePath = new ArrayList<>();
        for (String nodeId : path) {
            for (Node node : nodes) {
                if (node.getId().equals(nodeId)) {
                    nodePath.add(node);
                    break;
                }
            }
        }

        return new Pair<>(nodePath, System.currentTimeMillis() - startTime);
    }

    // Simple Pair class for Java implementation
    public static class Pair<F, S> {
        private final F first;
        private final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public F getFirst() {
            return first;
        }

        public S getSecond() {
            return second;
        }
    }
}
