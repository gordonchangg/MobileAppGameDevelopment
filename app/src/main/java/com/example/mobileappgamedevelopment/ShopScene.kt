package com.example.mobileappgamedevelopment

class ShopScene : IScene {
    override val entities: MutableList<Entity> = mutableListOf()
    override lateinit var entityManager: EntityManager
    override var sceneManager: SceneManager? = null

    private val path = listOf(
        floatArrayOf(-0.4f, 0.8f, 0f),  // Top-left corner
        floatArrayOf(0.4f, 0.8f, 0f),  // Top-right corner
        floatArrayOf(0.4f, -0.8f, 0f), // Bottom-right corner
        floatArrayOf(-0.4f, -0.8f, 0f) // Bottom-left corner
    )

    lateinit var toGameSceneButton: Entity

    override fun onSurfaceCreated() {
        repeat(5) { index ->
            val entity = entityManager.createEntity(R.drawable.placeholder_customer)
            entity.position = floatArrayOf(-0.8f, 0.8f, 0f) // Start at the first waypoint
            entity.scale = floatArrayOf(0.3f, 0.3f, 1f) // Smaller size for customers
            entity.userData["path"] = path // Attach the path to the entity
            entity.userData["progress"] = 0f // Track progress along the path
            entity.userData["speed"] = 0.5f // Random speed between 0.5 and 1.0
            entities.add(entity)
        }

        toGameSceneButton = entityManager.createEntity(R.drawable.placeholder_customer)
        toGameSceneButton.position = floatArrayOf(0f, -0.8f, 0f)
        toGameSceneButton.scale = floatArrayOf(0.5f, 0.5f, 0.5f)
        entities.add(toGameSceneButton)
    }

    override fun onSurfaceChanged() {

    }

    private fun interpolate(start: FloatArray, end: FloatArray, t: Float): FloatArray {
        return floatArrayOf(
            start[0] + (end[0] - start[0]) * t,
            start[1] + (end[1] - start[1]) * t,
            start[2] + (end[2] - start[2]) * t
        )
    }

    override fun update() {
        synchronized(entities) {
            for (entity in entities) {
                if (entity == toGameSceneButton) continue // Skip the button

                val path = entity.userData["path"] as List<FloatArray>
                var progress = entity.userData["progress"] as Float
                val speed = entity.userData["speed"] as Float

                progress += 0.0167f * speed

                while (progress >= path.size) {
                    progress -= path.size.toFloat()
                }

                val segmentIndex = progress.toInt()
                val t = progress - segmentIndex
                val start = path[segmentIndex % path.size]
                val end = path[(segmentIndex + 1) % path.size]

                entity.position = interpolate(start, end, t)

                entity.userData["progress"] = progress
            }
        }
    }

    override fun onActionDown(normalizedX: Float, normalizedY: Float) {
        synchronized(entities) {
            if(toGameSceneButton.contains(normalizedX, normalizedY)){
                sceneManager?.setScene(GameScene::class)
            }
        }
    }

    override fun onActionMove(normalizedDx: Float, normalizedDy: Float) {
        entityManager.moveSelectedEntity(normalizedDx, normalizedDy)
    }

    override fun onActionUp() {
        entityManager.setSelectedEntity(null)
    }
}