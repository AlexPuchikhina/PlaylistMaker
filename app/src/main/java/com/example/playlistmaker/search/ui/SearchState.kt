package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    data object Empty : SearchState
    data object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    data object NothingFound : SearchState
    data object Error : SearchState
    data class History(val tracks: List<Track>) : SearchState
}