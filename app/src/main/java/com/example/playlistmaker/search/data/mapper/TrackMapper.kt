package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.dto.TrackHistoryDto
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
fun TrackDto.toTrack(): Track {
    return Track(
        trackId = trackId,
        trackName = trackName.orEmpty(),
        artistName = artistName.orEmpty(),
        trackTime = timeFormat.format(trackTimeMillis ?: 0L),
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate?.take(4),
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}

fun TrackHistoryDto.toTrack() = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl
)

fun Track.toHistoryDto() = TrackHistoryDto(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl
)