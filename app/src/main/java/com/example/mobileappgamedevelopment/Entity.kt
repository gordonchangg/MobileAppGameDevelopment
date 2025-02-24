package com.example.mobileappgamedevelopment

import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Collections
import java.util.UUID

class Entity(
    val id: String = UUID.randomUUID().toString(),
    var position: MutableList<Float> = mutableListOf(0f, 0f, 0f), // x, y, z
    var scale: MutableList<Float> = mutableListOf(1f, 1f, 1f),     // scaleX, scaleY, scaleZ
    var rotation: Float = 0f,                             // Rotation angle in degrees
    var layerId: UInt = 0u,
    var textureId: Int = 0,                                   // Texture ID

    val userData: MutableMap<String, Any?> = mutableMapOf()
) {
    constructor() : this(UUID.randomUUID().toString(), mutableListOf(0f, 0f, 0f), mutableListOf(1f, 1f, 1f), 0f, 0, mutableMapOf())

    fun contains(x: Float, y: Float): Boolean {
        val halfWidth = scale[0] / 2
        val halfHeight = scale[1] / 2

        val left = position[0] - halfWidth
        val right = position[0] + halfWidth
        val bottom = position[1] - halfHeight
        val top = position[1] + halfHeight

        return x in left..right && y >= bottom && y <= top
    }
}

class EntityManager() {
    var background : Entity? = null
    val entities = Collections.synchronizedList(mutableListOf<Entity>())

    private var selectedEntity: Entity? = null

    fun selectEntity(x: Float, y: Float): Entity? {
        for (entity in entities) {
            if (entity.contains(x, y)) {
                return entity
            }
        }
        return null
    }

    fun createBackgroundEntity(resourceId: Int){
        background = Entity(textureId = resourceId)
        background?.position = mutableListOf(0.0f,0.0f,0.0f)

        background?.scale = mutableListOf(1.0f,2.0f,0.0f)
    }

    fun setBackgroundTexture(resourceId: Int) {
        background?.textureId = resourceId
    }

    fun setSelectedEntity(entity: Entity?) {
        selectedEntity = entity
    }

    fun moveSelectedEntity(dx: Float, dy: Float) {
        selectedEntity?.let {
            it.position[0] += dx
            it.position[1] += dy
        }
    }

    fun createEntity(resourceId: Int): Entity {
        val entity = Entity(textureId = resourceId)
        entities.add(entity)
        return entity
    }

    fun deleteEntity(entity: Entity) {
        synchronized(entities) {
            entities.remove(entity)
        }
    }
}