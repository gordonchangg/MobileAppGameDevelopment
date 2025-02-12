package com.example.mobileappgamedevelopment

import android.content.Context
import android.graphics.Path.Op
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var shader: OpenGLShader
    lateinit var entityManager: EntityManager
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.1f,0.1f,0.1f,1.0f)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        val vertexShaderCode = """
            uniform mat4 uMVPMatrix;
            attribute vec4 vPosition;
            attribute vec2 aTextureCoordinate;
            
            varying vec2 vTextureCoordinate;
            
            void main() {
                gl_Position = uMVPMatrix * vPosition;
                vTextureCoordinate = aTextureCoordinate;
            }
        """.trimIndent()

        // Define the fragment shader code
        val fragmentShaderCode = """
            precision mediump float;
            varying vec2 vTextureCoordinate;
            uniform sampler2D uTexture;
            void main() {
                gl_FragColor = texture2D(uTexture, vTextureCoordinate);
            }
        """.trimIndent()

        shader = OpenGLShader(vertexShaderCode, fragmentShaderCode)
        entityManager = EntityManager(context)

        entityManager.createBackgroundEntity(R.drawable.placeholder_bg)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        entityManager.processTextureLoadQueue()

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        entityManager.drawBackground(shader, vPMatrix)
        entityManager.drawEntities(shader, vPMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }
}