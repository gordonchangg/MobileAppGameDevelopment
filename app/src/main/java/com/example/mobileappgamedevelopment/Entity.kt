package com.example.mobileappgamedevelopment

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.view.WindowManager
import java.util.Collections

class Entity(
    var position: FloatArray = floatArrayOf(0f, 0f, 0f), // x, y, z
    var scale: FloatArray = floatArrayOf(1f, 1f, 1f),     // scaleX, scaleY, scaleZ
    var rotation: Float = 0f,                             // Rotation angle in degrees
    var textureId: Int                                   // Texture ID
) {
    fun contains(x: Float, y: Float): Boolean {
        val halfWidth = scale[0] / 2
        val halfHeight = scale[1] / 2

        val left = position[0] - halfWidth
        val right = position[0] + halfWidth
        val bottom = position[1] - halfHeight
        val top = position[1] + halfHeight

        return x in left..right && y >= bottom && y <= top
    }

    fun draw(shader: OpenGLShader, mvpMatrix: FloatArray) {
        // Use the shader program
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
        Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])
        Matrix.scaleM(modelMatrix, 0, scale[0], scale[1], scale[2])
        Matrix.rotateM(modelMatrix, 0, rotation, 0f, 0f, 1f)

        val finalMatrix = FloatArray(16)
        Matrix.multiplyMM(finalMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        val mvpMatrixHandle = shader.getUniformLocation("uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, finalMatrix, 0)

        val textureHandle = shader.getUniformLocation("uTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
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

class EntityManager(private val context: Context) {
    private var background : Entity? = null
    private val entities = Collections.synchronizedList(mutableListOf<Entity>())
    private val textures = mutableMapOf<Int, Int>()
    private val textureLoadQueue = mutableListOf<Pair<Int, Entity>>()
    private var selectedEntity: Entity? = null

    fun selectEntity(x: Float, y: Float): Entity? {
        for (entity in entities) {
            if (entity.contains(x, y)) {
                return entity
            }
        }
        return null
    }

    fun createBackgroundEntity(resourceId: Int){
        val textureId = textures.getOrPut(resourceId) {
            loadTexture(context, resourceId)
        }
        background = Entity(textureId = textureId)
        background?.position = floatArrayOf(0.0f,0.0f,0.0f)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = android.util.DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val aspectRatio = screenWidth.toFloat() / screenHeight
        background?.scale = floatArrayOf(1.0f,2.0f,0.0f)
    }

    fun setBackgroundTexture(resourceId: Int){
        val textureId = textures.getOrPut(resourceId) {
            loadTexture(context, resourceId)
        }
        background?.textureId = textureId
    }

    fun setSelectedEntity(entity: Entity?) {
        selectedEntity = entity
    }

    fun moveSelectedEntity(dx: Float, dy: Float) {
        selectedEntity?.let {
            it.position[0] += dx
            it.position[1] += dy
        }
    }

    fun createEntity(resourceId: Int): Entity {
        val entity = Entity(textureId = -1)
        textureLoadQueue.add(Pair(resourceId, entity))
        entities.add(entity)
        return entity
    }

    fun processTextureLoadQueue() {
        for ((resourceId, entity) in textureLoadQueue) {
            val textureId = textures.getOrPut(resourceId) {
                loadTexture(context, resourceId)
            }
            entity.textureId = textureId // Assign the loaded texture ID to the entity
        }
        textureLoadQueue.clear() // Clear the queue after processing
    }

    fun drawEntities(shader: OpenGLShader, mvpMatrix: FloatArray) {
        synchronized(entities) {
            for (entity in entities) {
                entity.draw(shader, mvpMatrix)
            }
        }
    }

    fun drawBackground(shader: OpenGLShader, mvpMatrix: FloatArray){
        background?.draw(shader,mvpMatrix)
    }
}