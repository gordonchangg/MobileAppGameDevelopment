package com.example.mobileappgamedevelopment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopScene : IScene {
    override val entities: MutableList<Entity> = mutableListOf()
    override lateinit var entityManager: EntityManager
    override var sceneManager: SceneManager? = null
    override val lines: MutableList<LineInfo> = mutableListOf()
    override lateinit var viewModel: MainViewModel


    private val path = listOf(
        floatArrayOf(-0.4f, 0.8f, 0f),
        floatArrayOf(0.4f, 0.8f, 0f),
        floatArrayOf(0.4f, -0.8f, 0f),
        floatArrayOf(-0.4f, -0.8f, 0f)
    )

    lateinit var toGameSceneButton: Entity

    override fun onSurfaceCreated() {
        repeat(5) { index ->
            val entity = entityManager.createEntity(R.drawable.placeholder_customer)
            entity.position = floatArrayOf(-0.8f, 0.8f, 0f)
            entity.scale = floatArrayOf(0.3f, 0.3f, 1f)
            entity.userData["path"] = path
            entity.userData["progress"] = 0f
            entity.userData["speed"] = 0.5f
            entities.add(entity)
        }

        toGameSceneButton = entityManager.createEntity(R.drawable.placeholder_customer)
        toGameSceneButton.position = floatArrayOf(0f, -0.8f, 0f)
        toGameSceneButton.scale = floatArrayOf(0.5f, 0.5f, 0.5f)
        entities.add(toGameSceneButton)

//        CoroutineScope(Dispatchers.Main).launch {
//            viewModel.updateDynamicText("New Dynamic Text!", true, Offset(0f, 0f))
//        }
        //viewModel.audioManager.playBGM(R.raw.bgm)
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
//        synchronized(entities) {
//            for (entity in entities) {
//                if (entity == toGameSceneButton) continue
//
//                val path = entity.userData["path"] as? List<FloatArray> ?: emptyList()
//                var progress = entity.userData["progress"] as Float
//                val speed = entity.userData["speed"] as Float
//
//                progress += 0.0167f * speed
//
//                while (progress >= path.size) {
//                    progress -= path.size.toFloat()
//                }
//
//                val segmentIndex = progress.toInt()
//                val t = progress - segmentIndex
//                val start = path[segmentIndex % path.size]
//                val end = path[(segmentIndex + 1) % path.size]
//
//                entity.position = interpolate(start, end, t)
//
//                entity.userData["progress"] = progress
//            }
//        }

        entityManager.setBackgroundTexture(R.drawable.placeholder_bg)
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