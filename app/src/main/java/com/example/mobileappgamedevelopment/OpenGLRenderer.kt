package com.example.mobileappgamedevelopment

import android.content.Context
import android.graphics.Path.Op
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val context: Context, val viewModel: MainViewModel) : GLSurfaceView.Renderer {

    private lateinit var shader: OpenGLShader
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val textures = mutableMapOf<Int, Int>()

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
        viewModel.entityManager.createBackgroundEntity(R.drawable.placeholder_bg)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        drawEntity(viewModel.entityManager.background!!, shader, vPMatrix)
        for(entity in viewModel.entityManager.entities){
            drawEntity(entity, shader, vPMatrix)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    private fun drawEntity(entity : Entity, shader : OpenGLShader, mvpMatrix: FloatArray){
        shader.use()

        // Get attribute locations
        val positionHandle = GLES20.glGetAttribLocation(shader.programId, "vPosition").also { handle ->
            GLES20.glEnableVertexAttribArray(handle)
            GLES20.glVertexAttribPointer(
                handle,
                3, // Number of coordinates per vertex (x, y, z)
                GLES20.GL_FLOAT,
                false,
                0,
                QuadBuffers.vertexBuffer // Use shared vertex buffer
            )
        }

        val textureCoordinateHandle = GLES20.glGetAttribLocation(shader.programId, "aTextureCoordinate").also { handle ->
            GLES20.glEnableVertexAttribArray(handle)
            GLES20.glVertexAttribPointer(
                handle,
                2, // Number of texture coordinates per vertex (u, v)
                GLES20.GL_FLOAT,
                false,
                0,
                QuadBuffers.textureBuffer // Use shared texture buffer
            )
        }

        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, entity.position[0], entity.position[1], entity.position[2])
        Matrix.scaleM(modelMatrix, 0, entity.scale[0], entity.scale[1], entity.scale[2])
        Matrix.rotateM(modelMatrix, 0, entity.rotation, 0f, 0f, 1f)

        val finalMatrix = FloatArray(16)
        Matrix.multiplyMM(finalMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        val mvpMatrixHandle = shader.getUniformLocation("uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, finalMatrix, 0)

        //check if texture is loaded, if not load it and store it in textures
        val textureID = textures.getOrPut(entity.textureId){
            loadTexture(context, entity.textureId)
        }

        val textureHandle = shader.getUniformLocation("uTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            QuadBuffers.indexBuffer.capacity(),
            GLES20.GL_UNSIGNED_SHORT,
            QuadBuffers.indexBuffer // Use shared index buffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle)
    }
}