package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.SearchResult

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun interface TracksConsumer {
        fun consume(result: SearchResult)
    }
}