package com.ankitghoshthecreator.map

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View

class GraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    val nodeRadius = 40f

    // Expanded to 15 nodes with strategic connections to show Dijkstra's advantage
    val nodes = listOf(
        Node("A", 150f, 150f),
        Node("B", 400f, 100f),
        Node("C", 650f, 150f),
        Node("D", 800f, 300f),
        Node("E", 700f, 500f),
        Node("F", 500f, 600f),
        Node("G", 250f, 500f),
        Node("H", 150f, 300f),
        Node("I", 400f, 300f),
        Node("J", 600f, 300f),
        Node("K", 300f, 200f),
        Node("L", 500f, 200f),
        Node("M", 400f, 450f),
        Node("N", 550f, 450f),
        Node("O", 250f, 350f)
    )

    // Define weighted edges for Dijkstra to show its advantage
    val edges = listOf(
        Edge("A", "B", 4),
        Edge("A", "H", 8),
        Edge("A", "K", 3),
        Edge("B", "C", 8),
        Edge("B", "L", 5),
        Edge("B", "K", 2),
        Edge("C", "D", 7),
        Edge("C", "J", 4),
        Edge("C", "L", 6),
        Edge("D", "E", 9),
        Edge("D", "J", 5),
        Edge("E", "F", 4),
        Edge("E", "J", 6),
        Edge("E", "N", 3),
        Edge("F", "G", 2),
        Edge("F", "M", 7),
        Edge("F", "N", 6),
        Edge("G", "H", 1),
        Edge("G", "M", 5),
        Edge("G", "O", 7),
        Edge("H", "O", 4),
        Edge("I", "J", 8),
        Edge("I", "K", 5),
        Edge("I", "L", 6),
        Edge("I", "M", 2),
        Edge("I", "O", 3),
        Edge("J", "L", 3),
        Edge("J", "N", 9),
        Edge("K", "O", 4),
        Edge("L", "N", 7),
        Edge("M", "N", 3),
        Edge("M", "O", 5)
    )

    // Make these properties accessible for reset functionality
    var currentPath: List<Node> = emptyList()
    var animationProgress = 0f

    var startNodeId: String? = null
    var endNodeId: String? = null
    var pathTime: Int = 0 // Store the time taken for the path

    fun animatePath(path: List<Node>, durationPerStep: Long) {
        currentPath = path
        pathTime = (path.size - 1) // Each step takes 1 second

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = (path.size - 1) * durationPerStep
            addUpdateListener {
                animationProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        // Draw connections with weights
        val connectionPaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 4f
        }

        val weightPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }

        edges.forEach { edge ->
            val fromNode = nodes.first { it.id == edge.from }
            val toNode = nodes.first { it.id == edge.to }

            canvas.drawLine(fromNode.x, fromNode.y, toNode.x, toNode.y, connectionPaint)

            // Draw weight in the middle of the edge
            val midX = (fromNode.x + toNode.x) / 2
            val midY = (fromNode.y + toNode.y) / 2
            canvas.drawText(edge.weight.toString(), midX, midY, weightPaint)
        }

        // Draw path
        if (currentPath.isNotEmpty()) {
            val pathPaint = Paint().apply {
                color = Color.RED
                strokeWidth = 8f
                style = Paint.Style.STROKE
            }

            val androidPath = Path().apply {
                moveTo(currentPath.first().x, currentPath.first().y)
                currentPath.drop(1).forEach { lineTo(it.x, it.y) }
            }

            val measure = PathMeasure(androidPath, false)
            val length = measure.length * animationProgress

            val partialPath = Path()
            measure.getSegment(0f, length, partialPath, true)
            canvas.drawPath(partialPath, pathPaint)
        }

        // Draw nodes with color based on selection
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }

        nodes.forEach { node ->
            // Determine node color based on selection
            val nodePaint = Paint().apply {
                when (node.id) {
                    startNodeId -> color = Color.YELLOW  // Start node is yellow
                    endNodeId -> color = Color.GREEN     // End node is green
                    else -> color = Color.BLUE           // Unselected nodes are blue
                }
                style = Paint.Style.FILL
            }

            canvas.drawCircle(node.x, node.y, nodeRadius, nodePaint)
            canvas.drawText(
                node.id,
                node.x,
                node.y + textPaint.textSize / 3,
                textPaint
            )
        }
    }
}
