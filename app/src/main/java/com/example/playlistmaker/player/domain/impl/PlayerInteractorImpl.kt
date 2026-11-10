package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    private var state = PlayerState.DEFAULT
    private var listener: PlayerInteractor.PlayerStateListener? = null

    override fun preparePlayer(url: String, listener: PlayerInteractor.PlayerStateListener) {
        this.listener = listener
        repository.prepare(
            url = url,
            onPrepared = { setState(PlayerState.PREPARED) },
            onCompletion = { setState(PlayerState.PREPARED) }
        )
    }

    override fun playbackControl() {
        when (state) {
            PlayerState.PLAYING -> pause()
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                repository.start()
                setState(PlayerState.PLAYING)
            }
            PlayerState.DEFAULT -> Unit
        }
    }

    override fun pause() {
        if (state == PlayerState.PLAYING) {
            repository.pause()
            setState(PlayerState.PAUSED)
        }
    }

    override fun release() {
        listener = null
        repository.release()
        state = PlayerState.DEFAULT
    }

    override fun getPlayerState(): PlayerState = state

    override fun getCurrentPosition(): Int = repository.getCurrentPosition()

    private fun setState(newState: PlayerState) {
        state = newState
        listener?.onStateChanged(newState)
    }
}