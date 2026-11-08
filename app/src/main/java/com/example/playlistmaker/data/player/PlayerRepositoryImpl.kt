package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerRepository
import java.io.IOException

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        try {
            mediaPlayer.setDataSource(url)
        } catch (e: IOException) {
            return
        } catch (e: IllegalArgumentException) {
            return
        }
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onCompletion() }
        mediaPlayer.prepareAsync()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition
}