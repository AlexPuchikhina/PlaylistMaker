package com.example.playlistmaker.search.domain.models

sealed interface SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult
    data object Error : SearchResult
}