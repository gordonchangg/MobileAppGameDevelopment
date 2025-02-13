package com.example.mobileappgamedevelopment

interface IScene {
    fun onSurfaceCreated()
    fun onDrawFrame()
    fun onSurfaceChanged()
    fun update()
}

class SceneManager{
    private var currentScene: IScene? = null

    fun setScene(scene: IScene){
        currentScene?.onSurfaceCreated()
        currentScene = scene
        currentScene?.onSurfaceCreated()
    }

    fun onDrawFrame(){
        currentScene?.onDrawFrame()
    }

    fun onSurfaceChanged(){
        currentScene?.onSurfaceChanged()
    }

    fun update(){
        currentScene?.update()
    }
}