package com.example.mobileappgamedevelopment

class GameScene : IScene {
    override val entities: MutableList<Entity> = mutableListOf()
    override lateinit var entityManager: EntityManager
    override var sceneManager: SceneManager? = null

    lateinit var toGameSceneButton: Entity

    override fun onSurfaceCreated() {
        repeat(10) { index ->
            val entity = entityManager.createEntity(R.drawable.placeholder_customer)
            entity.position = floatArrayOf((index - 1).toFloat(), 2f, 2f) // Example positions
            entity.scale = floatArrayOf(0.3f, 2f, 2f) // Example positions
            entities.add(entity)
        }

        toGameSceneButton = entityManager.createEntity(R.drawable.placeholder_customer)
        toGameSceneButton.position = floatArrayOf(0f, 0f, 0f)
        toGameSceneButton.scale = floatArrayOf(0.5f, 0.5f, 0.5f)
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
}