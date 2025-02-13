package com.example.mobileappgamedevelopment

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle

class OpenGLActvity : Activity() {
    private lateinit var gLView: GLSurfaceView
    private val viewModel = MainViewModel()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gLView = OpenGLView(this, viewModel)
        setContentView(gLView)
    }

}