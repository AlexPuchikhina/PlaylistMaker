package com.example.playlistmaker.network

import com.example.playlistmaker.Track
import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toTrack(): Track {
    val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    return Track(
        trackId = trackId,
        trackName = trackName.orEmpty(),
        artistName = artistName.orEmpty(),
        trackTime = timeFormat.format(trackTimeMillis ?: 0L),
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate?.take(4),
        primaryGenreName = primaryGenreName,
        country = country
    )
}