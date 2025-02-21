package com.example.mobileappgamedevelopment

import android.content.Context
import android.media.MediaPlayer

class AudioManager {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var context : Context

    fun setContext(context: Context){
        this.context = context
    }

    fun playBGM(audioId: Int) {
        mediaPlayer = MediaPlayer.create(context, audioId)

        mediaPlayer?.isLooping = true

        mediaPlayer?.start()
    }

    fun playAudio(audioId : Int) {
        mediaPlayer = MediaPlayer.create(context, audioId)

        mediaPlayer?.setOnCompletionListener {
            println("Playback completed")
        }

        mediaPlayer?.start()
    }

    fun stopAudio() {
        // Stop and release the MediaPlayer
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}