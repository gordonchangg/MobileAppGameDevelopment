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
    val gridMinY = 0.5f
    val gridMaxY = -0.8f
    val cellWidth = (gridMaxX - gridMinX) / gridWidth
    val cellHeight = (gridMaxY - gridMinY) / gridHeight
    val gridThickness = 0.01f
    val gridColor = floatArrayOf(0.1f, 0.1f, 0.1f, 1f)

    private var draggingEntity: Entity? = null  // Entity being moved
    private var isDragging = false  // Tracks if dragging is happening
    private var isHolding = false  // Tracks if user held long enough
    private val holdHandler = Handler(Looper.getMainLooper())  // Timer for hold detection

    private val holdRunnable = Runnable {
        if (draggingEntity != null) {
            isHolding = true
            isDragging = true  // Enable dragging
        }
    }
    //producers
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

        //addEntityToCell(3, 5, R.drawable.placeholder_customer)
        addEntityToCell(2, 3, producer_book)
        addEntityToCell(5, 5, producer_seed)
        addEntityToCell(3, 7, producer_wheatplant)

    }

    override fun onSurfaceChanged() {

    }

    override fun update() {

    }

    override fun onActionDown(normalizedX: Float, normalizedY: Float) {
        synchronized(entities) {
            for (entity in entities.reversed()) { // Check topmost entity first
                if (entity.contains(normalizedX, normalizedY)) {
                    draggingEntity = entity
                    isDragging = false
                    isHolding = false

                    // Start hold detection
                    holdHandler.postDelayed(holdRunnable, 300) // 300ms delay for hold detection
                    return
                }
            }
        }
    }

    override fun onActionMove(normalizedDx: Float, normalizedDy: Float) {
        if (isDragging && draggingEntity != null) {
            draggingEntity!!.position[0] += normalizedDx
            draggingEntity!!.position[1] += normalizedDy
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

        val searchOrder = mutableListOf<Pair<Int, Int>>()

        // First, check direct adjacent spaces
        searchOrder.addAll(listOf(
            Pair(producerX - 1, producerY), // Left
            Pair(producerX + 1, producerY), // Right
            Pair(producerX, producerY - 1), // Up
            Pair(producerX, producerY + 1)  // Down
        ))

        // Then, expand outward in a circular pattern
        for (radius in 1..Math.max(gridWidth, gridHeight)) {
            for (dx in -radius..radius) {
                for (dy in -radius..radius) {
                    if (dx == 0 && dy == 0) continue // Skip producer's own cell
                    searchOrder.add(Pair(producerX + dx, producerY + dy))
                }
            }
        }

        // Find the first available spot from searchOrder
        for ((x, y) in searchOrder) {
            if (x in 0 until gridWidth && y in 0 until gridHeight && !occupiedCells.contains(x to y)) {
                return x to y
            }
        }

        return -1 to -1 // No available space
    }

    override fun onActionUp() {
        draggingEntity?.let { entity ->
            val gridX = ((entity.position[0] - gridMinX) / cellWidth).toInt().coerceIn(0, gridWidth - 1)
            val gridY = ((entity.position[1] - gridMinY) / cellHeight).toInt().coerceIn(0, gridHeight - 1)

            val snappedPosition = getCellCenter(gridX, gridY, gridMinX, gridMinY, cellWidth, cellHeight)
            entity.position = snappedPosition
        }
        draggingEntity = null
    }

    fun getCellCenter(xIndex: Int, yIndex: Int, gridMinX: Float, gridMinY: Float, cellWidth: Float, cellHeight: Float): FloatArray {
        val centerX = gridMinX + (xIndex + 0.5f) * cellWidth
        val centerY = gridMinY + (yIndex + 0.5f) * cellHeight
        return floatArrayOf(centerX, centerY, 0f) // Z-coordinate is 0
    }

    fun addEntityToCell(xIndex: Int, yIndex: Int, textureId: Int) {
        val cellCenter = getCellCenter(xIndex, yIndex, gridMinX, gridMinY, cellWidth, cellHeight)

        //original pikachu
        val entity = entityManager.createEntity(textureId)
        entity.position = cellCenter
        entity.scale = floatArrayOf(0.05f, 0.05f, 1f)
        entities.add(entity)
    }
}