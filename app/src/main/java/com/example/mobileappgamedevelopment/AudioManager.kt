/**
 * @file AudioManager.kt
 * @brief Manages audio playback including background music (BGM) and sound effects.
 *
 * This class provides methods to play background music (BGM) and sound effects (SFX)
 * in an Android application. It ensures that only one background track is played at a time
 * and allows stopping of audio when needed.
 */

package com.example.mobileappgamedevelopment

import android.content.Context
import android.media.MediaPlayer

/**
 * @class AudioManager
 * @brief Handles audio playback including background music (BGM) and sound effects (SFX).
 */
class AudioManager {
    /// MediaPlayer instance for playing audio.
    private var mediaPlayer: MediaPlayer? = null

    /// Application context required for audio playback.
    private lateinit var context: Context

    /**
     * @brief Sets the application context required for playing audio.
     * @param context The application context.
     */
    fun setContext(context: Context) {
        this.context = context
    }

    /**
     * @brief Plays background music (BGM).
     * @param audioId The resource ID of the audio file to be played.
     *
     * Stops any currently playing audio before playing the new BGM.
     * The BGM is set to loop indefinitely.
     */
    fun playBGM(audioId: Int) {
        stopAudio() // Stop any currently playing audio
        mediaPlayer = MediaPlayer.create(context, audioId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    /**
     * @brief Plays a short sound effect (SFX).
     * @param audioId The resource ID of the audio file to be played.
     *
     * Plays the sound effect once and prints a message upon completion.
     */
    fun playAudio(audioId: Int) {
        mediaPlayer = MediaPlayer.create(context, audioId)
        mediaPlayer?.setOnCompletionListener {
            println("Playback completed")
        }
        mediaPlayer?.start()
    }

    /**
     * @brief Stops and releases the currently playing audio.
     *
     * This function stops playback and releases the media player to free up resources.
     */
    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
