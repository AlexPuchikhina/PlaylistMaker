package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.SearchResult
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>(SearchState.Empty)
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var searchText = ""
    private var lastSearchQuery: String? = null
    private var isSearchFocused = false

    private val searchRunnable = Runnable {
        val query = searchText.trim()
        if (query.isNotEmpty()) search(query)
    }

    fun onSearchTextChanged(text: String) {
        if (text == searchText) return
        searchText = text
        handler.removeCallbacks(searchRunnable)
        if (text.isEmpty()) {
            lastSearchQuery = null
            showHistoryOrEmpty()
        } else {
            if (stateLiveData.value is SearchState.History) {
                stateLiveData.value = SearchState.Empty
            }
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MS)
        }
    }

    fun onSearchFocusChanged(hasFocus: Boolean) {
        isSearchFocused = hasFocus
        if (searchText.isEmpty()) showHistoryOrEmpty()
    }

    fun onSearchAction() {
        val query = searchText.trim()
        if (query.isNotEmpty()) {
            handler.removeCallbacks(searchRunnable)
            search(query)
        }
    }

    fun onRefreshClicked() {
        lastSearchQuery?.let { search(it) }
    }

    fun onClearHistoryClicked() {
        searchHistoryInteractor.clearHistory()
        showHistoryOrEmpty()
    }

    fun onTrackClicked(track: Track) {
        searchHistoryInteractor.addTrack(track)
        if (stateLiveData.value is SearchState.History) showHistoryOrEmpty()
    }

    private fun search(query: String) {
        lastSearchQuery = query
        stateLiveData.value = SearchState.Loading

        tracksInteractor.searchTracks(query) { result ->
            handler.post {
                if (query != lastSearchQuery) return@post
                stateLiveData.value = when (result) {
                    is SearchResult.Success ->
                        if (result.tracks.isEmpty()) SearchState.NothingFound
                        else SearchState.Content(result.tracks)
                    SearchResult.Error -> SearchState.Error
                }
            }
        }
    }

    private fun showHistoryOrEmpty() {
        val history = searchHistoryInteractor.getHistory()
        stateLiveData.value =
            if (isSearchFocused && searchText.isEmpty() && history.isNotEmpty()) {
                SearchState.History(history)
            } else {
                SearchState.Empty
            }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 2000L

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    Creator.provideTracksInteractor(),
                    Creator.provideSearchHistoryInteractor()
                )
            }
        }
    }
}