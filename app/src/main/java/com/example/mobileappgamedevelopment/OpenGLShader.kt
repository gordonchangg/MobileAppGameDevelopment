package com.example.mobileappgamedevelopment

import android.opengl.GLES20

class OpenGLShader {
    var programId: Int = 0

    constructor(vertexShaderCode: String, fragmentShaderCode: String) {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        programId = GLES20.glCreateProgram().also { program ->
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)

            GLES20.glLinkProgram(program)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                throw RuntimeException("Error linking program:\n" + GLES20.glGetProgramInfoLog(program))
            }

            GLES20.glDeleteShader(vertexShader)
            GLES20.glDeleteShader(fragmentShader)
        }
    }

    fun use() {
        GLES20.glUseProgram(programId)
    }

    fun getUniformLocation(name: String): Int {
        return GLES20.glGetUniformLocation(programId, name)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)

            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                throw RuntimeException("Error compiling shader:\n" + GLES20.glGetShaderInfoLog(shader))
            }
        }
    }
}