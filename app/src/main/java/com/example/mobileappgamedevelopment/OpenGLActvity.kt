package com.example.mobileappgamedevelopment

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle

class OpenGLActvity : Activity() {
    private lateinit var gLView: GLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gLView = OpenGLView(this)
        setContentView(gLView)
    }

}