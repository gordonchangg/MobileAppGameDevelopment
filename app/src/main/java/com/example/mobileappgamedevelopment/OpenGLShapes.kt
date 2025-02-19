package com.example.mobileappgamedevelopment

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
    0.0f, 0.622008459f, 0.0f,      // top
    -0.5f, -0.311004243f, 0.0f,    // bottom left
    0.5f, -0.311004243f, 0.0f      // bottom right
)

class Triangle {

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"
}

var squareCoords = floatArrayOf(
    -0.5f,  0.5f, 0.0f,      // top left
    -0.5f, -0.5f, 0.0f,      // bottom left
    0.5f, -0.5f, 0.0f,      // bottom right
    0.5f,  0.5f, 0.0f       // top right
)

class Square {

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    // initialize vertex byte buffer for shape coordinates
    private val vertexBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    // initialize byte buffer for the draw list
    private val drawListBuffer: ShortBuffer =
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }
}

class DrawableObject(
    private val vertices: FloatArray,
    private val textureCoordinates: FloatArray,
    private val indices: ShortArray,
    private val shader: OpenGLShader,
    private val textureId: Int
) {
    private var vertexBuffer: java.nio.FloatBuffer
    private var textureBuffer: java.nio.FloatBuffer
    private var indexBuffer: java.nio.ShortBuffer

    init {
        // Vertex buffer
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }

        // Texture buffer
        textureBuffer = ByteBuffer.allocateDirect(textureCoordinates.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(textureCoordinates)
                position(0)
            }

        // Index buffer
        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(java.nio.ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply {
                put(indices)
                position(0)
            }
    }

    fun draw(mvpMatrix: FloatArray) {
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
                vertexBuffer
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
                textureBuffer
            )
        }

        // Get uniform locations
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shader.programId, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        val textureHandle = GLES20.glGetUniformLocation(shader.programId, "uTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        // Draw the quad
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        // Disable vertex arrays
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle)
    }
}

object QuadBuffers {
    // Quad vertices (shared for all quads)
    private val vertices = floatArrayOf(
        -0.5f,  0.5f, 0.0f,  // Top-left
        -0.5f, -0.5f, 0.0f,  // Bottom-left
        0.5f, -0.5f, 0.0f,  // Bottom-right
        0.5f,  0.5f, 0.0f   // Top-right
    )

    // Texture coordinates (shared for all quads)
    private val textureCoordinates = floatArrayOf(
        0.0f, 0.0f,  // Top-left
        0.0f, 1.0f,  // Bottom-left
        1.0f, 1.0f,  // Bottom-right
        1.0f, 0.0f   // Top-right
    )

    // Indices to form two triangles from the quad vertices
    private val indices = shortArrayOf(
        0, 1, 2,  // First triangle
        0, 2, 3   // Second triangle
    )

    // Vertex buffer
    val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        .order(java.nio.ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(vertices)
            position(0)
        }

    // Texture buffer
    val textureBuffer: FloatBuffer = ByteBuffer.allocateDirect(textureCoordinates.size * 4)
        .order(java.nio.ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(textureCoordinates)
            position(0)
        }

    // Index buffer
    val indexBuffer: ShortBuffer = ByteBuffer.allocateDirect(indices.size * 2)
        .order(java.nio.ByteOrder.nativeOrder())
        .asShortBuffer()
        .apply {
            put(indices)
            position(0)
        }
}

data class LineInfo(
    val start: FloatArray, // Start point (x, y, z)
    val end: FloatArray,   // End point (x, y, z)
    val thickness: Float,  // Line thickness
    val color: FloatArray  // Line color (RGBA)
)