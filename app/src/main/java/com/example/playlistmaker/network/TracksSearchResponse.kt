package com.example.playlistmaker.network

data class TracksSearchResponse(
    val resultCount: Int?,
    val results: List<TrackDto>?
)