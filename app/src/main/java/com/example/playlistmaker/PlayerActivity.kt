package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var btnPlay: ImageButton
    private lateinit var tvProgress: TextView

    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                tvProgress.text = timeFormat.format(mediaPlayer.currentPosition)
                handler.postDelayed(this, PROGRESS_UPDATE_DELAY_MS)
            }
        }
    }

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

        btnPlay = findViewById(R.id.btnPlay)
        tvProgress = findViewById(R.id.tvProgress)
        btnPlay.setOnClickListener { playbackControl() }

        preparePlayer(track.previewUrl)
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressRunnable)
        mediaPlayer.release()
    }

    private fun preparePlayer(previewUrl: String?) {
        btnPlay.isEnabled = false
        if (previewUrl.isNullOrEmpty()) return

        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.setOnPreparedListener {
            btnPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            handler.removeCallbacks(updateProgressRunnable)
            btnPlay.setImageResource(R.drawable.ic_play_100x100)
            tvProgress.text = getString(R.string.player_progress_default)
        }
        mediaPlayer.prepareAsync()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        btnPlay.setImageResource(R.drawable.ic_pause_100x100)
        handler.post(updateProgressRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateProgressRunnable)
        btnPlay.setImageResource(R.drawable.ic_play_100x100)
    }

    companion object {
        const val TRACK_KEY = "TRACK_KEY"

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val PROGRESS_UPDATE_DELAY_MS = 300L
    }
}