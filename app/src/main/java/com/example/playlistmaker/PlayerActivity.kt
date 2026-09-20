package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trackJson = intent.getStringExtra(TRACK_KEY)
        if (trackJson == null) {
            finish()
            return
        }

        setContentView(R.layout.activity_player)
        val track = Gson().fromJson(trackJson, Track::class.java)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val ivCover = findViewById<ImageView>(R.id.ivCover)
        val cornerRadiusPx = resources.getDimensionPixelSize(R.dimen.track_artwork_corner_radius)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .error(R.drawable.ic_track_placeholder)
            .transform(RoundedCorners(cornerRadiusPx))
            .into(ivCover)

        findViewById<TextView>(R.id.tvTrackName).text = track.trackName
        findViewById<TextView>(R.id.tvArtistName).text = track.artistName
        findViewById<TextView>(R.id.tvDurationValue).text = track.trackTime

        val rowAlbum = findViewById<View>(R.id.rowAlbum)
        if (track.collectionName.isNullOrEmpty()) {
            rowAlbum.visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.tvAlbumValue).text = track.collectionName
        }

        val rowYear = findViewById<View>(R.id.rowYear)
        if (track.releaseDate.isNullOrEmpty()) {
            rowYear.visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.tvYearValue).text = track.releaseDate
        }

        findViewById<TextView>(R.id.tvGenreValue).text = track.primaryGenreName
        findViewById<TextView>(R.id.tvCountryValue).text = track.country
    }

    companion object {
        const val TRACK_KEY = "TRACK_KEY"
    }
}