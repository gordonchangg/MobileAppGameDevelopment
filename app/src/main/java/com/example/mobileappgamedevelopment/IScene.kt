package com.example.mobileappgamedevelopment

import kotlin.reflect.KClass

interface IScene {
    val entities: MutableList<Entity>
    var entityManager: EntityManager
    var sceneManager: SceneManager?
    var viewModel: MainViewModel

    val lines: MutableList<LineInfo>
    fun onSurfaceCreated()

    fun onEnter()
    fun onSurfaceChanged()
    fun update()

    fun onActionDown(normalizedX: Float, normalizedY: Float)
    fun onActionMove(normalizedDx: Float, normalizedDy: Float)
    fun onActionUp()

    fun deleteEntity(entity: Entity){
        entityManager.deleteEntity(entity)
        entities.remove(entity)
    }
}

class SceneManager(private val entityManager: EntityManager){
    private var currentScene: IScene? = null
    private val sceneCache = mutableMapOf<KClass<*>, IScene>()

    fun setScene(sceneClass: KClass<out IScene>, viewModel: MainViewModel) {
        val (scene, isNewScene) = if (sceneClass in sceneCache) {
            sceneCache[sceneClass]!! to false
        } else {
            val newScene = when (sceneClass) {
                ShopScene::class -> ShopScene()
                GameScene::class -> GameScene()
                else -> throw IllegalArgumentException("Unknown scene type")
            }
            sceneCache[sceneClass] = newScene
            newScene to true
        }

//        if (currentScene != null && currentScene != scene) {
//            currentScene?.onSurfaceCreated()
//        }

        currentScene = scene
        currentScene?.viewModel = viewModel
        currentScene?.entityManager = entityManager
        currentScene?.sceneManager = this

        if (isNewScene) {
            currentScene?.onSurfaceCreated()
        }

        currentScene?.onEnter()
    }

    fun  onSurfaceChanged(){
        currentScene?.onSurfaceChanged()
    }

    fun update(){
        currentScene?.update()
    }

    fun getEntities(): List<Entity> {
        // Ensure thread-safe access to the entities list
        val entities = currentScene?.entities ?: emptyList<Entity>()
        return synchronized(entities) {
            ArrayList(entities) // Return a copy of the list
        }
    }

    fun getLines(): List<LineInfo>{
        val lines = currentScene?.lines ?: emptyList<LineInfo>()
        return synchronized(lines) {
            ArrayList(lines) // Return a copy of the list
        }
    }

    fun onActionDown(normalizedX: Float, normalizedY: Float) {
        currentScene?.onActionDown(normalizedX, normalizedY)
    }

    fun onActionMove(normalizedDx: Float, normalizedDy: Float) {
        currentScene?.onActionMove(normalizedDx, normalizedDy)
    }

    fun onActionUp(){
        currentScene?.onActionUp()
    }
}