package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerState

interface PlayerInteractor {
    fun preparePlayer(url: String, listener: PlayerStateListener)
    fun playbackControl()
    fun pause()
    fun release()
    fun getPlayerState(): PlayerState
    fun getCurrentPosition(): Int

    fun interface PlayerStateListener {
        fun onStateChanged(state: PlayerState)
    }
}