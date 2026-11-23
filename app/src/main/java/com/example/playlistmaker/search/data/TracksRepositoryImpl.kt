package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.SearchResult
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.mapper.toTrack

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): SearchResult {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode != 200) return SearchResult.Error
        val tracks = (response as? TracksSearchResponse)?.results.orEmpty().map { it.toTrack() }
        return SearchResult.Success(tracks)
    }
}