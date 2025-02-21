package com.example.mobileappgamedevelopment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import coil3.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ShopScene : IScene {
    override val entities: MutableList<Entity> = mutableListOf()
    override lateinit var entityManager: EntityManager
    override var sceneManager: SceneManager? = null
    override val lines: MutableList<LineInfo> = mutableListOf()
    override lateinit var viewModel: MainViewModel

    lateinit var table: Entity
    lateinit var table1: Entity
    lateinit var table2: Entity
    lateinit var plate: Entity


    private val path = listOf(
        floatArrayOf(0.3f, 0.45f, 0f),
        floatArrayOf(0.3f, 0.25f, 0f),
        floatArrayOf(-0.13f, 0.25f, 0f),
//        floatArrayOf(0.4f, -0.8f, 0f),
//        floatArrayOf(-0.4f, -0.8f, 0f)
    )

    lateinit var toGameSceneButton: Entity

    override fun onSurfaceCreated() {

        //plate
        plate  =entityManager.createEntity(R.drawable.plate)
        plate.position = floatArrayOf(-0.33f, 0.41f, 0f)
        plate.scale = floatArrayOf(0.15f, 0.1f, 0.1f)
        entities.add(plate)

        plate  =entityManager.createEntity(R.drawable.plate)
        plate.position = floatArrayOf(-0.15f, 0.41f, 0f)
        plate.scale = floatArrayOf(0.15f, 0.1f, 0.1f)
        entities.add(plate)

        plate  =entityManager.createEntity(R.drawable.plate)
        plate.position = floatArrayOf(0.03f, 0.41f, 0f)
        plate.scale = floatArrayOf(0.15f, 0.1f, 0.1f)
        entities.add(plate)

        repeat(1) { index ->
            val entity = entityManager.createEntity(R.drawable.placeholder_customer)
            entity.position = floatArrayOf(-0.8f, 0.8f, 0f)
            entity.scale = floatArrayOf(0.2f, 0.2f, 1f)
            entity.userData["path"] = path
            entity.userData["progress"] = 0f
            entity.userData["speed"] = 0.5f
            entities.add(entity)
        }

        toGameSceneButton = entityManager.createEntity(R.drawable.bakery)
        toGameSceneButton.position = floatArrayOf(0.3f, -0.87f, 0f)
        toGameSceneButton.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(toGameSceneButton)

        //tables
        table  =entityManager.createEntity(R.drawable.table)
        table.position = floatArrayOf(-0.16f, -0.1f, 0f)
        table.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(table)

        table  =entityManager.createEntity(R.drawable.table)
        table.position = floatArrayOf(-0.16f, -0.5f, 0f)
        table.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(table)

        table  =entityManager.createEntity(R.drawable.table)
        table.position = floatArrayOf(0.16f, -0.1f, 0f)
        table.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(table)

        table  =entityManager.createEntity(R.drawable.table)
        table.position = floatArrayOf(0.16f, -0.5f, 0f)
        table.scale = floatArrayOf(0.22f, 0.22f, 0.25f)
        entities.add(table)


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
                if (entity == toGameSceneButton) continue

                // Safely retrieve user data fields
                val path = entity.userData["path"] as? List<FloatArray> ?: emptyList()
                var progress = entity.userData["progress"] as? Float ?: 0.1f
                val speed = entity.userData["speed"] as? Float ?: 0.1f

                // Skip the entity if the path is empty
                if (path.isEmpty()) {
                    continue
                }

                // Update progress
                progress += 0.0167f * speed
                while (progress >= path.size) {
                    progress -= path.size.toFloat()
                }

                // Interpolate position
                val segmentIndex = progress.toInt()
                val t = progress - segmentIndex
                val start = path[segmentIndex % path.size]
                val end = path[(segmentIndex + 1) % path.size]
                entity.position = interpolate(start, end, t)

                // Update progress in user data
                entity.userData["progress"] = progress
            }

        }

        entityManager.setBackgroundTexture(R.drawable.shopbg)
    }

    override fun onActionDown(normalizedX: Float, normalizedY: Float) {
        viewModel.audioManager.playAudio(R.raw.click)
        synchronized(entities) {
            if(toGameSceneButton.contains(normalizedX, normalizedY)){

                sceneManager?.setScene(GameScene::class, viewModel)
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