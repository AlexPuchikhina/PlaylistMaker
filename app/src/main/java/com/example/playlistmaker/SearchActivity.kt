package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.network.RetrofitClient
import com.example.playlistmaker.network.TracksSearchResponse
import com.example.playlistmaker.network.toTrack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var rvTracks: RecyclerView
    private lateinit var placeholderContainer: LinearLayout
    private lateinit var ivPlaceholderIcon: ImageView
    private lateinit var tvPlaceholderTitle: TextView
    private lateinit var tvPlaceholderMessage: TextView
    private lateinit var btnRefresh: Button

    private lateinit var adapter: TrackAdapter

    private var searchText: String = ""
    private var lastSearchQuery: String? = null
    private var searchCall: Call<TracksSearchResponse>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        etSearch = findViewById(R.id.etSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        rvTracks = findViewById(R.id.rvTracks)
        placeholderContainer = findViewById(R.id.placeholderContainer)
        ivPlaceholderIcon = findViewById(R.id.ivPlaceholderIcon)
        tvPlaceholderTitle = findViewById(R.id.tvPlaceholderTitle)
        tvPlaceholderMessage = findViewById(R.id.tvPlaceholderMessage)
        btnRefresh = findViewById(R.id.btnRefresh)

        adapter = TrackAdapter()
        rvTracks.layoutManager = LinearLayoutManager(this)
        rvTracks.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
                btnClearSearch.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) {
                    clearSearchResults()
                }
            }
        })

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = etSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }

        btnClearSearch.setOnClickListener {
            etSearch.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
            clearSearchResults()
        }

        btnRefresh.setOnClickListener {
            lastSearchQuery?.let { query -> performSearch(query) }
        }
    }

    private fun performSearch(query: String) {
        searchCall?.cancel()
        lastSearchQuery = query

        val call = RetrofitClient.iTunesApiService.search(query)
        searchCall = call
        call.enqueue(object : Callback<TracksSearchResponse> {
            override fun onResponse(
                call: Call<TracksSearchResponse>,
                response: Response<TracksSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.results.orEmpty().map { it.toTrack() }
                    if (tracks.isEmpty()) {
                        showEmptyState()
                    } else {
                        showContent(tracks)
                    }
                } else {
                    showErrorState()
                }
            }

            override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
                if (call.isCanceled) return
                showErrorState()
            }
        })
    }

    private fun showContent(tracks: List<Track>) {
        placeholderContainer.visibility = View.GONE
        rvTracks.visibility = View.VISIBLE
        adapter.updateTracks(tracks)
    }

    private fun showEmptyState() {
        rvTracks.visibility = View.GONE
        adapter.updateTracks(emptyList())
        placeholderContainer.visibility = View.VISIBLE
        ivPlaceholderIcon.setImageResource(R.drawable.ic_placeholder_nothing_found_120x120)
        tvPlaceholderTitle.text = getString(R.string.placeholder_nothing_found_title)
        tvPlaceholderMessage.visibility = View.GONE
        btnRefresh.visibility = View.GONE
    }

    private fun showErrorState() {
        rvTracks.visibility = View.GONE
        adapter.updateTracks(emptyList())
        placeholderContainer.visibility = View.VISIBLE
        ivPlaceholderIcon.setImageResource(R.drawable.ic_placeholder_no_connection_120x120)
        tvPlaceholderTitle.text = getString(R.string.placeholder_no_connection_title)
        tvPlaceholderMessage.text = getString(R.string.placeholder_no_connection_message)
        tvPlaceholderMessage.visibility = View.VISIBLE
        btnRefresh.visibility = View.VISIBLE
    }

    private fun clearSearchResults() {
        searchCall?.cancel()
        lastSearchQuery = null
        rvTracks.visibility = View.GONE
        placeholderContainer.visibility = View.GONE
        adapter.updateTracks(emptyList())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        etSearch.setText(searchText)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
    }
}