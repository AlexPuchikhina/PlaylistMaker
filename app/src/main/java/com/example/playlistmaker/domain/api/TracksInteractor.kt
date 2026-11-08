package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.SearchResult

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun interface TracksConsumer {
        fun consume(result: SearchResult)
    }
}