package com.ankitghoshthecreator.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class aStar {

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

        // Create node map for quick lookup
        Map<String, Node> nodeMap = new HashMap<>();
        for (Node node : nodes) {
            nodeMap.put(node.getId(), node);
        }

        // A* implementation
        Map<String, Integer> gScore = new HashMap<>(); // Cost from start to current node
        Map<String, Integer> fScore = new HashMap<>(); // Estimated total cost (g + h)
        Map<String, String> parentMap = new HashMap<>();

        // Priority queue for A* (ordered by fScore)
        PriorityQueue<Pair<String, Integer>> openSet = new PriorityQueue<>((a, b) -> a.getSecond() - b.getSecond());

        // Initialize scores
        for (Node node : nodes) {
            gScore.put(node.getId(), node.getId().equals(startId) ? 0 : Integer.MAX_VALUE);
            fScore.put(node.getId(), node.getId().equals(startId) ? heuristic(nodeMap.get(startId), nodeMap.get(endId)) : Integer.MAX_VALUE);
        }

        openSet.add(new Pair<>(startId, fScore.get(startId)));

        while (!openSet.isEmpty()) {
            String current = openSet.poll().getFirst();

            if (current.equals(endId)) {
                break;
            }

            if (adjacencyMap.containsKey(current)) {
                for (Pair<String, Integer> neighborInfo : adjacencyMap.get(current)) {
                    String neighbor = neighborInfo.getFirst();
                    int weight = neighborInfo.getSecond();

                    int tentativeGScore = gScore.get(current) + weight;

                    if (tentativeGScore < gScore.get(neighbor)) {
                        parentMap.put(neighbor, current);
                        gScore.put(neighbor, tentativeGScore);
                        fScore.put(neighbor, tentativeGScore + heuristic(nodeMap.get(neighbor), nodeMap.get(endId)));

                        // Add to open set with updated fScore
                        openSet.add(new Pair<>(neighbor, fScore.get(neighbor)));
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
            nodePath.add(nodeMap.get(nodeId));
        }

        return new Pair<>(nodePath, System.currentTimeMillis() - startTime);
    }

    // Heuristic function: Euclidean distance between nodes
    private static int heuristic(Node a, Node b) {
        return (int) Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
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
