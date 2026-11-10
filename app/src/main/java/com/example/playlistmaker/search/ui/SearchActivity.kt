package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel

    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var rvTracks: RecyclerView
    private lateinit var placeholderContainer: LinearLayout
    private lateinit var ivPlaceholderIcon: ImageView
    private lateinit var tvPlaceholderTitle: TextView
    private lateinit var tvPlaceholderMessage: TextView
    private lateinit var btnRefresh: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var historyContainer: LinearLayout
    private lateinit var rvHistory: RecyclerView
    private lateinit var btnClearHistory: Button

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getFactory()
        )[SearchViewModel::class.java]

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        etSearch = findViewById(R.id.etSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        rvTracks = findViewById(R.id.rvTracks)
        placeholderContainer = findViewById(R.id.placeholderContainer)
        ivPlaceholderIcon = findViewById(R.id.ivPlaceholderIcon)
        tvPlaceholderTitle = findViewById(R.id.tvPlaceholderTitle)
        tvPlaceholderMessage = findViewById(R.id.tvPlaceholderMessage)
        btnRefresh = findViewById(R.id.btnRefresh)
        progressBar = findViewById(R.id.progressBar)
        historyContainer = findViewById(R.id.historyContainer)
        rvHistory = findViewById(R.id.rvHistory)
        btnClearHistory = findViewById(R.id.btnClearHistory)

        adapter = TrackAdapter(onTrackClick = { track -> onTrackClicked(track) })
        rvTracks.layoutManager = LinearLayoutManager(this)
        rvTracks.adapter = adapter

        historyAdapter = TrackAdapter(onTrackClick = { track -> onTrackClicked(track) })
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter

        etSearch.doAfterTextChanged { text ->
            btnClearSearch.isVisible = !text.isNullOrEmpty()
            viewModel.onSearchTextChanged(text?.toString().orEmpty())
        }

        etSearch.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFocusChanged(hasFocus)
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchAction()
                true
            } else {
                false
            }
        }

        btnClearSearch.setOnClickListener {
            etSearch.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
        }

        btnRefresh.setOnClickListener { viewModel.onRefreshClicked() }
        btnClearHistory.setOnClickListener { viewModel.onClearHistoryClicked() }

        viewModel.observeState().observe(this) { state -> render(state) }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun render(state: SearchState) {
        progressBar.isVisible = state is SearchState.Loading
        rvTracks.isVisible = state is SearchState.Content
        historyContainer.isVisible = state is SearchState.History
        placeholderContainer.isVisible =
            state is SearchState.NothingFound || state is SearchState.Error

        when (state) {
            is SearchState.Content -> adapter.updateTracks(state.tracks)
            is SearchState.History -> historyAdapter.updateTracks(state.tracks)
            SearchState.NothingFound -> showNothingFound()
            SearchState.Error -> showError()
            SearchState.Loading, SearchState.Empty -> adapter.updateTracks(emptyList())
        }
    }

    private fun showNothingFound() {
        adapter.updateTracks(emptyList())
        ivPlaceholderIcon.setImageResource(R.drawable.ic_placeholder_nothing_found_120x120)
        tvPlaceholderTitle.text = getString(R.string.placeholder_nothing_found_title)
        tvPlaceholderMessage.visibility = View.GONE
        btnRefresh.visibility = View.GONE
    }

    private fun showError() {
        adapter.updateTracks(emptyList())
        ivPlaceholderIcon.setImageResource(R.drawable.ic_placeholder_no_connection_120x120)
        tvPlaceholderTitle.text = getString(R.string.placeholder_no_connection_title)
        tvPlaceholderMessage.text = getString(R.string.placeholder_no_connection_message)
        tvPlaceholderMessage.visibility = View.VISIBLE
        btnRefresh.visibility = View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MS)
        }
        return current
    }

    private fun onTrackClicked(track: Track) {
        if (!clickDebounce()) return
        viewModel.onTrackClicked(track)

        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(PlayerActivity.TRACK_KEY, Gson().toJson(track))
        startActivity(intent)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MS = 1000L
    }
}