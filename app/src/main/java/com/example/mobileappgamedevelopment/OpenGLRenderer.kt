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

        val entityA = entityManager.createEntity(R.drawable.placeholder_customer)
        entityA.position = floatArrayOf(-0.5f, 0f, 0f)
        entityA.scale = floatArrayOf(0.5f, 0.5f, 1f)
        entityA.rotation = 45f

        val entityB = entityManager.createEntity(R.drawable.placeholder_customer)
        entityB.position = floatArrayOf(0.5f, 0f, 0f)
        entityB.scale = floatArrayOf(0.75f, 0.75f, 1f)
        entityB.rotation = -30f
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

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