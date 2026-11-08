package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.TrackHistoryDto
import com.example.playlistmaker.data.mapper.toHistoryDto
import com.example.playlistmaker.data.mapper.toTrack
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<TrackHistoryDto>>() {}.type
        val dtos: List<TrackHistoryDto> = gson.fromJson(json, type)
        return dtos.map { it.toTrack() }
    }

    override fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks.map { it.toHistoryDto() })
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .remove(HISTORY_KEY)
            .apply()
    }

    companion object {
        private const val HISTORY_KEY = "search_history_key"
    }
}