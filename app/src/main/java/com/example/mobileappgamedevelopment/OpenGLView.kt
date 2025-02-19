package com.example.mobileappgamedevelopment

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class OpenGLView(context: Context, private val viewModel: MainViewModel) : GLSurfaceView(context) {
    private val renderer : OpenGLRenderer

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        val aspectRatio = width.toFloat() / height

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                val normalizedX = ((x / width) * 2 - 1) * aspectRatio
                val normalizedY = 1 - (y / height) * 2

                viewModel.sceneManager.onActionDown(normalizedX, normalizedY)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                val normalizedDx = (dx / width) * 2 * aspectRatio
                val normalizedDy = -(dy / height) * 2

                viewModel.sceneManager.onActionMove(normalizedDx, normalizedDy)

                requestRender()
            }

            MotionEvent.ACTION_UP -> {
                viewModel.sceneManager.onActionUp()
            }
        }

        previousX = x
        previousY = y
        return true
    }

    init {
        setEGLContextClientVersion(2)

        renderer = OpenGLRenderer(context, viewModel)
        setRenderer(renderer)
    }
}

@Composable
fun OpengGLComposable(modifier: Modifier = Modifier, viewModel: MainViewModel){
    AndroidView(
        factory = { context ->
            OpenGLView(context, viewModel)
        },
        modifier = modifier
    )
}