package com.example.mobileappgamedevelopment

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class OpenGLView(context: Context, viewModel: MainViewModel) : GLSurfaceView(context) {
    private val renderer : OpenGLRenderer

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        val aspectRatio = width.toFloat() / height
        val normalizedX = ((x / width) * 2 - 1) * aspectRatio
        val normalizedY = 1 - (y / height) * 2

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {

                val selectedEntity = renderer.viewModel.entityManager.selectEntity(normalizedX, normalizedY)
                if(selectedEntity != null)
                    renderer.viewModel.entityManager.setSelectedEntity(selectedEntity)
                else {
                    var entity = renderer.viewModel.entityManager.createEntity(R.drawable.placeholder_customer)
                    entity.position = floatArrayOf(normalizedX, normalizedY, 0f)
                    entity.scale = floatArrayOf(0.5f, 0.5f, 1f)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                val normalizedDx = (dx / width) * 2 * aspectRatio
                val normalizedDy = -(dy / height) * 2

                renderer.viewModel.entityManager.moveSelectedEntity(normalizedDx, normalizedDy)

                requestRender()
            }

            MotionEvent.ACTION_UP -> {
                renderer.viewModel.entityManager.setSelectedEntity(null)
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