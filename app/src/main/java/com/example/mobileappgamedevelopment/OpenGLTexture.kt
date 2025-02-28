package com.example.mobileappgamedevelopment

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import coil3.Bitmap


fun loadTexture(context: Context, resourceId: Int): Int {
    val textureHandle = IntArray(1)
    GLES20.glGenTextures(1, textureHandle, 0)

    if (textureHandle[0] == 0) {
        throw RuntimeException("Error generating texture name")
    }

    val options = BitmapFactory.Options()
    options.inScaled = false // No pre-scaling

    // Load the bitmap from resources
    val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

    // Bind the texture
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

    // Set texture parameters
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

    // Load the bitmap into the bound texture
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

    // Recycle the bitmap, since its data has been loaded into OpenGL
    bitmap.recycle()

    return textureHandle[0]
}