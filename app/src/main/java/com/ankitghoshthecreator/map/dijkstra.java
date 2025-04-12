package com.ankitghoshthecreator.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class dijkstra {

    public static Pair<List<Node>, Long> findPath(List<Node> nodes, List<Edge> edges, String startId, String endId) {
        long startTime = System.currentTimeMillis();

        // Create adjacency list with weights from edges
        Map<String, List<Pair<String, Integer>>> adjacencyMap = new HashMap<>();
        for (Edge edge : edges) {
            if (!adjacencyMap.containsKey(edge.getFrom())) {
                adjacencyMap.put(edge.getFrom(), new ArrayList<>());
            }
            adjacencyMap.get(edge.getFrom()).add(new Pair<>(edge.getTo(), edge.getWeight()));

            // For undirected graph (can go both ways)
            if (!adjacencyMap.containsKey(edge.getTo())) {
                adjacencyMap.put(edge.getTo(), new ArrayList<>());
            }
            adjacencyMap.get(edge.getTo()).add(new Pair<>(edge.getFrom(), edge.getWeight()));
        }

        // Dijkstra implementation
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        // Priority queue for Dijkstra
        PriorityQueue<Pair<String, Integer>> pq = new PriorityQueue<>((a, b) -> a.getSecond() - b.getSecond());

        // Initialize distances
        for (Node node : nodes) {
            distances.put(node.getId(), node.getId().equals(startId) ? 0 : Integer.MAX_VALUE);
        }

        pq.add(new Pair<>(startId, 0));

        while (!pq.isEmpty()) {
            Pair<String, Integer> current = pq.poll();
            String currentId = current.getFirst();
            int dist = current.getSecond();

            if (currentId.equals(endId)) break;

            if (visited.contains(currentId)) continue;
            visited.add(currentId);

            if (adjacencyMap.containsKey(currentId)) {
                for (Pair<String, Integer> neighborInfo : adjacencyMap.get(currentId)) {
                    String neighbor = neighborInfo.getFirst();
                    int weight = neighborInfo.getSecond();

                    int newDist = dist + weight;
                    if (newDist < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        distances.put(neighbor, newDist);
                        parentMap.put(neighbor, currentId);
                        pq.add(new Pair<>(neighbor, newDist));
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
