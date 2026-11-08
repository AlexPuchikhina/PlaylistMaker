package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.SearchResult

interface TracksRepository {
    fun searchTracks(expression: String): SearchResult
}