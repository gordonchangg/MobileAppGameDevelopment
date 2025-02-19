package com.example.mobileappgamedevelopment

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

        addEntityToCell(3, 5, R.drawable.placeholder_customer)
    }

    override fun onSurfaceChanged() {

    }

    override fun update() {

    }

    override fun onActionDown(normalizedX: Float, normalizedY: Float) {
        synchronized(entities) {

        }
    }

    override fun onActionMove(normalizedDx: Float, normalizedDy: Float) {
    }

    override fun onActionUp() {
    }

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
}