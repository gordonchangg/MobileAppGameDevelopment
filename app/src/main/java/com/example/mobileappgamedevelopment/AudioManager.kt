package com.example.mobileappgamedevelopment

import android.content.Context
import android.media.MediaPlayer

class AudioManager {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var context : Context

    fun setContext(context: Context){
        this.context = context
    }

    fun playAudio(audioId : Int) {
        // Initialize MediaPlayer with a resource
        mediaPlayer = MediaPlayer.create(context, audioId)

        // Set up listeners (optional)
        mediaPlayer?.setOnCompletionListener {
            println("Playback completed")
        }

        // Start playback
        mediaPlayer?.start()
    }

    fun stopAudio() {
        // Stop and release the MediaPlayer
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}