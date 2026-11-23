package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    track: Track,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val trackLiveData = MutableLiveData(track)
    fun observeTrack(): LiveData<Track> = trackLiveData

    private val stateLiveData = MutableLiveData(
        PlayerScreenState(PlayerState.DEFAULT, START_PROGRESS)
    )
    fun observeState(): LiveData<PlayerScreenState> = stateLiveData

    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (playerInteractor.getPlayerState() == PlayerState.PLAYING) {
                stateLiveData.value = PlayerScreenState(PlayerState.PLAYING, currentProgress())
                handler.postDelayed(this, PROGRESS_UPDATE_DELAY_MS)
            }
        }
    }

    init {
        val previewUrl = track.previewUrl
        if (!previewUrl.isNullOrEmpty()) {
            playerInteractor.preparePlayer(previewUrl) { state -> onPlayerStateChanged(state) }
        }
    }

    fun onPlayClicked() {
        playerInteractor.playbackControl()
    }

    fun onPause() {
        playerInteractor.pause()
    }

    private fun onPlayerStateChanged(state: PlayerState) {
        handler.removeCallbacks(updateProgressRunnable)
        when (state) {
            PlayerState.DEFAULT, PlayerState.PREPARED ->
                stateLiveData.value = PlayerScreenState(state, START_PROGRESS)
            PlayerState.PLAYING -> {
                stateLiveData.value = PlayerScreenState(state, currentProgress())
                handler.post(updateProgressRunnable)
            }
            PlayerState.PAUSED ->
                stateLiveData.value = PlayerScreenState(state, currentProgress())
        }
    }

    private fun currentProgress(): String =
        timeFormat.format(playerInteractor.getCurrentPosition())

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateProgressRunnable)
        playerInteractor.release()
    }

    companion object {
        private const val START_PROGRESS = "00:00"
        private const val PROGRESS_UPDATE_DELAY_MS = 300L

        fun getFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(track, Creator.providePlayerInteractor())
            }
        }
    }
}