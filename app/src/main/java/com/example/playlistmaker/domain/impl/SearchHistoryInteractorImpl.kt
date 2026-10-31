package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(): List<Track> = repository.getHistory()

    override fun addTrack(track: Track) {
        val history = repository.getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        repository.saveHistory(history.take(MAX_HISTORY_SIZE))
    }

    override fun clearHistory() = repository.clearHistory()

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}