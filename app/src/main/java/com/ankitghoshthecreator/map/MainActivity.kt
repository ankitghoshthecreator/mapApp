package com.ankitghoshthecreator.map

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var graphView: GraphView

    private lateinit var tvFrom: TextView
    private lateinit var tvTo: TextView
    private lateinit var tvBfsTime: TextView
    private lateinit var tvDijkstraTime: TextView
    private lateinit var tvAStarTime: TextView
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
        tvAStarTime = findViewById(R.id.tvAStarTime)
        tvPathTime = findViewById(R.id.tvPathTime)

        graphView.setOnTouchListener { _, event ->
            handleNodeSelection(event.x, event.y)
            true
        }

        findViewById<Button>(R.id.btnBFS).setOnClickListener {
            runBFS()
        }

        findViewById<Button>(R.id.btnDijkstra).setOnClickListener {
            runDijkstra()
        }

        findViewById<Button>(R.id.btnAStar).setOnClickListener {
            runAStar()
        }

        // Add Reset button functionality
        findViewById<Button>(R.id.btnReset).setOnClickListener {
            resetAll()
        }
    }

    // Reset function
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
        tvAStarTime.text = "A* Time: 0 ms"
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
                tvAStarTime.text = "A* Time: 0 ms"
                tvPathTime.text = "Path Time: 0 sec"
            } else {
                endNode = selectedNode
                graphView.endNodeId = selectedNode.id
                tvTo.text = "To: ${selectedNode.id}"
            }
            graphView.invalidate()
        }
    }

    private fun runBFS() {
        if (startNode == null || endNode == null) return

        val result = bfs.findPath(graphView.nodes, graphView.edges, startNode!!.id, endNode!!.id)
        val path = result.first
        val timeTaken = result.second

        graphView.animatePath(path, 1000L)
        tvBfsTime.text = "BFS Time: ${timeTaken} ms"
        tvPathTime.text = "Path Time: ${path.size - 1} sec"
    }

    private fun runDijkstra() {
        if (startNode == null || endNode == null) return

        val result = dijkstra.findPath(graphView.nodes, graphView.edges, startNode!!.id, endNode!!.id)
        val path = result.first
        val timeTaken = result.second

        graphView.animatePath(path, 1000L)
        tvDijkstraTime.text = "Dijkstra Time: ${timeTaken} ms"
        tvPathTime.text = "Path Time: ${path.size - 1} sec"
    }

    private fun runAStar() {
        if (startNode == null || endNode == null) return

        val result = aStar.findPath(graphView.nodes, graphView.edges, startNode!!.id, endNode!!.id)
        val path = result.first
        val timeTaken = result.second

        graphView.animatePath(path, 1000L)
        tvAStarTime.text = "A* Time: ${timeTaken} ms"
        tvPathTime.text = "Path Time: ${path.size - 1} sec"
    }
}
