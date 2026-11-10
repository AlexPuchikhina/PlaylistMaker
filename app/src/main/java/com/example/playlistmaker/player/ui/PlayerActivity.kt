package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel

    private lateinit var btnPlay: ImageButton
    private lateinit var tvProgress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trackJson = intent.getStringExtra(TRACK_KEY)
        if (trackJson == null) {
            finish()
            return
        }

        setContentView(R.layout.activity_player)
        val track = Gson().fromJson(trackJson, Track::class.java)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory(track)
        )[PlayerViewModel::class.java]

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        btnPlay = findViewById(R.id.btnPlay)
        tvProgress = findViewById(R.id.tvProgress)
        btnPlay.setOnClickListener { viewModel.onPlayClicked() }

        viewModel.observeTrack().observe(this) { showTrack(it) }
        viewModel.observeState().observe(this) { render(it) }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun showTrack(track: Track) {
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

        findViewById<View>(R.id.rowAlbum).isVisible = !track.collectionName.isNullOrEmpty()
        findViewById<TextView>(R.id.tvAlbumValue).text = track.collectionName

        findViewById<View>(R.id.rowYear).isVisible = !track.releaseDate.isNullOrEmpty()
        findViewById<TextView>(R.id.tvYearValue).text = track.releaseDate

        findViewById<TextView>(R.id.tvGenreValue).text = track.primaryGenreName
        findViewById<TextView>(R.id.tvCountryValue).text = track.country
    }

    private fun render(state: PlayerScreenState) {
        btnPlay.isEnabled = state.playerState != PlayerState.DEFAULT
        btnPlay.setImageResource(
            if (state.playerState == PlayerState.PLAYING) R.drawable.ic_pause_100x100
            else R.drawable.ic_play_100x100
        )
        tvProgress.text = state.progress
    }

    companion object {
        const val TRACK_KEY = "TRACK_KEY"
    }
}