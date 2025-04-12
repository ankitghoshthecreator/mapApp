package com.ankitghoshthecreator.map

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var graphView: GraphView

    private lateinit var tvFrom: TextView
    private lateinit var tvTo: TextView
    private lateinit var tvBfsTime: TextView
    private lateinit var tvDijkstraTime: TextView
    private lateinit var tvPathTime: TextView

    private var startNode: Node? = null
    private var endNode: Node? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        graphView = findViewById(R.id.graphView)

        tvFrom = findViewById(R.id.tvFrom)
        tvTo = findViewById(R.id.tvTo)
        tvBfsTime = findViewById(R.id.tvBfsTime)
        tvDijkstraTime = findViewById(R.id.tvDijkstraTime)
        tvPathTime = findViewById(R.id.tvPathTime)

        graphView.setOnTouchListener { _, event ->
            handleNodeSelection(event.x, event.y)
            true
        }

        findViewById<Button>(R.id.btnBFS).setOnClickListener {
            calculatePath(::bfs)
        }

        findViewById<Button>(R.id.btnDijkstra).setOnClickListener {
            calculatePath(::dijkstra)
        }

        // Add Reset button functionality
        findViewById<Button>(R.id.btnReset).setOnClickListener {
            resetAll()
        }
    }

    // New reset function
    private fun resetAll() {
        // Reset node selections
        startNode = null
        endNode = null
        graphView.startNodeId = null
        graphView.endNodeId = null

        // Reset text displays
        tvFrom.text = "From:"
        tvTo.text = "To:"
        tvBfsTime.text = "BFS Time: 0 ms"
        tvDijkstraTime.text = "Dijkstra Time: 0 ms"
        tvPathTime.text = "Path Time: 0 sec"

        // Reset path animation
        graphView.currentPath = emptyList()
        graphView.animationProgress = 0f
        graphView.pathTime = 0

        // Force redraw
        graphView.invalidate()
    }

    private fun handleNodeSelection(x: Float, y: Float) {
        graphView.nodes.firstOrNull {
            Math.hypot((it.x - x).toDouble(), (it.y - y).toDouble()) < graphView.nodeRadius
        }?.let { selectedNode ->
            if (startNode == null) {
                startNode = selectedNode
                graphView.startNodeId = selectedNode.id
                tvFrom.text = "From: ${selectedNode.id}"
                // Reset times when new selection starts
                tvBfsTime.text = "BFS Time: 0 ms"
                tvDijkstraTime.text = "Dijkstra Time: 0 ms"
                tvPathTime.text = "Path Time: 0 sec"
            } else {
                endNode = selectedNode
                graphView.endNodeId = selectedNode.id
                tvTo.text = "To: ${selectedNode.id}"
            }
            graphView.invalidate()
        }
    }

    private fun calculatePath(algorithm: (String, String) -> Pair<List<Node>, Long>) {
        if (startNode == null || endNode == null) return

        val (path, timeTaken) = algorithm(startNode!!.id, endNode!!.id)

        graphView.animatePath(path, 1000L)

        // Update algorithm execution time
        when (algorithm) {
            ::bfs -> tvBfsTime.text = "BFS Time: ${timeTaken} ms"
            ::dijkstra -> tvDijkstraTime.text = "Dijkstra Time: ${timeTaken} ms"
        }

        // Update path time (1 second per edge)
        tvPathTime.text = "Path Time: ${path.size - 1} sec"

        // Don't reset selection - keep nodes highlighted
        // Only reset when a new start node is selected
    }

    private fun bfs(startId: String, endId: String): Pair<List<Node>, Long> {
        val startTime = System.currentTimeMillis()

        // Create adjacency list from edges
        val adjacencyMap = mutableMapOf<String, MutableList<String>>()
        graphView.edges.forEach { edge ->
            if (!adjacencyMap.containsKey(edge.from)) {
                adjacencyMap[edge.from] = mutableListOf()
            }
            adjacencyMap[edge.from]?.add(edge.to)

            // For undirected graph (can go both ways)
            if (!adjacencyMap.containsKey(edge.to)) {
                adjacencyMap[edge.to] = mutableListOf()
            }
            adjacencyMap[edge.to]?.add(edge.from)
        }

        // BFS implementation
        val queue: Queue<String> = LinkedList()
        val visited = mutableSetOf<String>()
        val parentMap = mutableMapOf<String, String>()

        queue.add(startId)
        visited.add(startId)

        while (queue.isNotEmpty()) {
            val current = queue.poll()

            if (current == endId) break

            adjacencyMap[current]?.forEach { neighbor ->
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    parentMap[neighbor] = current
                    queue.add(neighbor)
                }
            }
        }

        // Reconstruct path
        val path = mutableListOf<String>()
        var current = endId

        while (current != startId) {
            path.add(0, current)
            current = parentMap[current] ?: break
        }

        path.add(0, startId)

        // Convert to Node objects
        val nodePath = path.map { nodeId ->
            graphView.nodes.first { it.id == nodeId }
        }

        return Pair(nodePath, System.currentTimeMillis() - startTime)
    }

    private fun dijkstra(startId: String, endId: String): Pair<List<Node>, Long> {
        val startTime = System.currentTimeMillis()

        // Create adjacency list with weights from edges
        val adjacencyMap = mutableMapOf<String, MutableList<Pair<String, Int>>>()
        graphView.edges.forEach { edge ->
            if (!adjacencyMap.containsKey(edge.from)) {
                adjacencyMap[edge.from] = mutableListOf()
            }
            adjacencyMap[edge.from]?.add(Pair(edge.to, edge.weight))

            // For undirected graph (can go both ways)
            if (!adjacencyMap.containsKey(edge.to)) {
                adjacencyMap[edge.to] = mutableListOf()
            }
            adjacencyMap[edge.to]?.add(Pair(edge.from, edge.weight))
        }

        // Dijkstra implementation
        val distances = mutableMapOf<String, Int>()
        val parentMap = mutableMapOf<String, String>()
        val visited = mutableSetOf<String>()

        // Priority queue for Dijkstra
        val pq = PriorityQueue<Pair<String, Int>>(compareBy { it.second })

        // Initialize distances
        graphView.nodes.forEach { node ->
            distances[node.id] = if (node.id == startId) 0 else Int.MAX_VALUE
        }

        pq.add(Pair(startId, 0))

        while (pq.isNotEmpty()) {
            val (current, dist) = pq.poll()

            if (current == endId) break

            if (current in visited) continue
            visited.add(current)

            adjacencyMap[current]?.forEach { (neighbor, weight) ->
                val newDist = dist + weight
                if (newDist < (distances[neighbor] ?: Int.MAX_VALUE)) {
                    distances[neighbor] = newDist
                    parentMap[neighbor] = current
                    pq.add(Pair(neighbor, newDist))
                }
            }
        }

        // Reconstruct path
        val path = mutableListOf<String>()
        var current = endId

        while (current != startId) {
            path.add(0, current)
            current = parentMap[current] ?: break
        }

        path.add(0, startId)

        // Convert to Node objects
        val nodePath = path.map { nodeId ->
            graphView.nodes.first { it.id == nodeId }
        }

        return Pair(nodePath, System.currentTimeMillis() - startTime)
    }
}

