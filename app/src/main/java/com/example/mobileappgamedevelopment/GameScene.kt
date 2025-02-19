package com.example.mobileappgamedevelopment

import android.os.Handler
import android.os.Looper

class GameScene : IScene {
    override val entities: MutableList<Entity> = mutableListOf()
    override lateinit var entityManager: EntityManager
    override var sceneManager: SceneManager? = null
    override val lines: MutableList<LineInfo> = mutableListOf()

    val gridWidth = 8
    val gridHeight = 10
    val gridMinX = -0.4f
    val gridMaxX = 0.4f
    val gridMinY = 0.4f
    val gridMaxY = -0.6f
    val cellWidth = (gridMaxX - gridMinX) / gridWidth
    val cellHeight = (gridMaxY - gridMinY) / gridHeight
    val gridThickness = 0.01f
    val gridColor = floatArrayOf(0.737f, 0.478f, 0.349f, 1f)

    val lightColor = floatArrayOf(0.95f, 0.85f, 0.60f, 1f) // Light beige
    val darkColor = floatArrayOf(0.80f, 0.60f, 0.40f, 1f) // Darker beige

    val cellColors = Array(gridWidth) { x ->
        Array(gridHeight) { y ->
            if ((x + y) % 2 == 0) lightColor else darkColor
        }
    }

    private var draggingEntity: Entity? = null
    private var isDragging = false
    private var isHolding = false
    private val holdHandler = Handler(Looper.getMainLooper())

    private val holdRunnable = Runnable {
        if (draggingEntity != null) {
            isHolding = true
            isDragging = true  // Enable dragging mode
        }
    }

    // Producers
    val producer_seed = R.drawable.seedpack
    val producer_wheatplant = R.drawable.wheatplant
    val producer_book = R.drawable.book

    // Mapping producers to their respective ingredients
    private val producerToIngredient = mapOf(
        producer_seed to R.drawable.seed,
        producer_wheatplant to R.drawable.wheat,
        producer_book to R.drawable.paper
    )

    override fun onSurfaceCreated() {
        val lightColor = floatArrayOf(0.863f, 0.733f, 0.537f, 1f) // Extracted light beige
        val darkColor = floatArrayOf(0.835f, 0.702f, 0.510f, 1f)

        for (x in 0 until gridWidth) {
            for (y in 0 until gridHeight) {
                val color = if ((x + y) % 2 == 0) lightColor else darkColor

                // Calculate the center position of the square
                val centerX = gridMinX + x * cellWidth + cellWidth / 2
                val centerY = gridMinY + y * cellHeight + cellHeight / 2

                // Use thick lines to fill the cell
                val thicknessX = cellWidth * 0.95f // Almost full width
                val thicknessY = cellHeight * 0.95f // Almost full height

                // Create filled rectangles using horizontal and vertical thick lines
                lines.add(LineInfo(floatArrayOf(centerX - cellWidth / 2, centerY, 0f), floatArrayOf(centerX + cellWidth / 2, centerY, 0f), thicknessY, color))
                lines.add(LineInfo(floatArrayOf(centerX, centerY - cellHeight / 2, 0f), floatArrayOf(centerX, centerY + cellHeight / 2, 0f), thicknessX, color))
            }
        }

        // Draw grid lines on top
        for (i in 0..gridHeight) {
            val y = gridMinY + i * cellHeight
            val start = floatArrayOf(gridMinX, y, 0f)
            val end = floatArrayOf(gridMaxX, y, 0f)
            lines.add(LineInfo(start, end, gridThickness, gridColor))
        }

        for (i in 0..gridWidth) {
            val x = gridMinX + i * cellWidth
            val start = floatArrayOf(x, gridMinY, 0f)
            val end = floatArrayOf(x, gridMaxY, 0f)
            lines.add(LineInfo(start, end, gridThickness, gridColor))
        }

        // Add game entities
        addEntityToCell(2, 3, producer_book)
        addEntityToCell(5, 5, producer_seed)
        addEntityToCell(3, 7, producer_wheatplant)
    }



    override fun onSurfaceChanged() {}

    override fun update() {}

    override fun onActionDown(normalizedX: Float, normalizedY: Float) {
        synchronized(entities) {
            for (entity in entities.reversed()) {
                if (entity.contains(normalizedX, normalizedY)) {
                    draggingEntity = entity
                    isDragging = false
                    isHolding = false

                    // Start hold detection (300ms to start dragging)
                    holdHandler.postDelayed(holdRunnable, 300)
                    return
                }
            }
        }
    }
    fun isCellOccupied(xIndex: Int, yIndex: Int): Boolean {
        return entities.any { entity ->
            val entityX = ((entity.position[0] - gridMinX) / cellWidth).toInt()
            val entityY = ((entity.position[1] - gridMinY) / cellHeight).toInt()
            entityX == xIndex && entityY == yIndex
        }
    }

    override fun onActionMove(normalizedDx: Float, normalizedDy: Float) {
        if (isDragging && draggingEntity != null) {
            draggingEntity!!.position[0] += normalizedDx
            draggingEntity!!.position[1] += normalizedDy
        }
    }


    override fun onActionUp() {
        holdHandler.removeCallbacks(holdRunnable) // Stop hold detection

        if (draggingEntity != null) {
            if (!isHolding) {
                val ingredientTexture = producerToIngredient[draggingEntity!!.textureId]
                if (ingredientTexture != null) {
                    val (xIndex, yIndex) = findNextAvailableGridCellNearProducer(draggingEntity!!)
                    if (xIndex != -1 && yIndex != -1) {
                        addEntityToCell(xIndex, yIndex, ingredientTexture) // Spawn ingredient
                    }
                }
            } else {
                val gridX = ((draggingEntity!!.position[0] - gridMinX) / cellWidth).toInt().coerceIn(0, gridWidth - 1)
                val gridY = ((draggingEntity!!.position[1] - gridMinY) / cellHeight).toInt().coerceIn(0, gridHeight - 1)

                draggingEntity!!.position = getCellCenter(gridX, gridY, gridMinX, gridMinY, cellWidth, cellHeight)
            }
        }

        // Reset states
        draggingEntity = null
        isDragging = false
        isHolding = false
    }


    fun getCellCenter(xIndex: Int, yIndex: Int, gridMinX: Float, gridMinY: Float, cellWidth: Float, cellHeight: Float): FloatArray {
        val centerX = gridMinX + (xIndex + 0.5f) * cellWidth
        val centerY = gridMinY + (yIndex + 0.5f) * cellHeight
        return floatArrayOf(centerX, centerY, 0f) // Z-coordinate is 0
    }

    fun addEntityToCell(xIndex: Int, yIndex: Int, textureId: Int) {
        if (!isCellOccupied(xIndex, yIndex)) {
            val cellCenter = getCellCenter(xIndex, yIndex, gridMinX, gridMinY, cellWidth, cellHeight)
            val entity = entityManager.createEntity(textureId)
            entity.position = cellCenter
            entity.scale = floatArrayOf(0.05f, 0.05f, 1f)
            entities.add(entity)
        }
    }

    fun findNextAvailableGridCellNearProducer(producer: Entity): Pair<Int, Int> {
        val producerX = ((producer.position[0] - gridMinX) / cellWidth).toInt()
        val producerY = ((producer.position[1] - gridMinY) / cellHeight).toInt()

        val occupiedCells = entities.map { entity ->
            val xIndex = ((entity.position[0] - gridMinX) / cellWidth).toInt()
            val yIndex = ((entity.position[1] - gridMinY) / cellHeight).toInt()
            xIndex to yIndex
        }.toSet()

        val searchOrder = listOf(
            Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1), // Adjacent cells
            Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1) // Diagonal cells
        )

        for ((dx, dy) in searchOrder) {
            val newX = producerX + dx
            val newY = producerY + dy
            if (newX in 0 until gridWidth && newY in 0 until gridHeight && !occupiedCells.contains(newX to newY)) {
                return newX to newY
            }
        }
        return -1 to -1 // No available space nearby
    }
}
