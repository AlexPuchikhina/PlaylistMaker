package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.models.PlayerState

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