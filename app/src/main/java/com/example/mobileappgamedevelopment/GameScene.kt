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

        addEntityToCell(2, 3, producer_book)
        addEntityToCell(5, 5, producer_seed)
        addEntityToCell(3, 7, producer_wheatplant)
    }

    override fun onSurfaceChanged() {}

    override fun update() {}

    /**
     * ✅ When clicking:
     * - If it's a **Producer**, prepare to spawn an **Ingredient**.
     * - If held for **300ms**, enable **dragging mode**.
     */
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

    /**
     * ✅ Moves the entity if the user is **holding and dragging**.
     */
    override fun onActionMove(normalizedDx: Float, normalizedDy: Float) {
        if (isDragging && draggingEntity != null) {
            draggingEntity!!.position[0] += normalizedDx
            draggingEntity!!.position[1] += normalizedDy
        }
    }

    /**
     * ✅ When the user releases:
     * - **If tapped** → Spawns an ingredient near the producer.
     * - **If dragged** → Snaps the entity to the grid.
     */
    override fun onActionUp() {
        holdHandler.removeCallbacks(holdRunnable) // Stop hold detection

        if (draggingEntity != null) {
            if (!isHolding) {
                // ✅ If the user **tapped**, it's a Producer → Spawn ingredient
                val ingredientTexture = producerToIngredient[draggingEntity!!.textureId]
                if (ingredientTexture != null) {
                    val (xIndex, yIndex) = findNextAvailableGridCellNearProducer(draggingEntity!!)
                    if (xIndex != -1 && yIndex != -1) {
                        addEntityToCell(xIndex, yIndex, ingredientTexture) // Spawn ingredient
                    }
                }
            } else {
                // ✅ If the user **held and dragged**, snap to the grid
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

    /**
     * ✅ Calculates the **center position** of a grid cell.
     */
    fun getCellCenter(xIndex: Int, yIndex: Int, gridMinX: Float, gridMinY: Float, cellWidth: Float, cellHeight: Float): FloatArray {
        val centerX = gridMinX + (xIndex + 0.5f) * cellWidth
        val centerY = gridMinY + (yIndex + 0.5f) * cellHeight
        return floatArrayOf(centerX, centerY, 0f) // Z-coordinate is 0
    }

    fun addEntityToCell(xIndex: Int, yIndex: Int, textureId: Int) {
        val cellCenter = getCellCenter(xIndex, yIndex, gridMinX, gridMinY, cellWidth, cellHeight)
        val entity = entityManager.createEntity(textureId)
        entity.position = cellCenter
        entity.scale = floatArrayOf(0.05f, 0.05f, 1f)
        entities.add(entity)
    }

    /**
     * ✅ Finds the next available grid position near the producer.
     */
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
