package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvArtistAndTime: TextView = itemView.findViewById(R.id.tvArtistAndTime)

    fun bind(track: Track) {
        tvTrackName.text = track.trackName
        tvArtistAndTime.text = itemView.context.getString(
            R.string.track_artist_and_time,
            track.artistName,
            track.trackTime
        )

        val cornerRadiusPx = itemView.resources.getDimensionPixelSize(R.dimen.track_artwork_corner_radius)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .error(R.drawable.ic_track_placeholder)
            .transform(RoundedCorners(cornerRadiusPx))
            .into(ivArtwork)
    }
}