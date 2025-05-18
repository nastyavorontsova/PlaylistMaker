package com.practicum.playlistmaker1.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.app.Event
import com.practicum.playlistmaker1.search.data.dto.NetworkResult
import com.practicum.playlistmaker1.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker1.search.domain.api.SuspendTrackInteractor
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(
    private val suspendTrackInteractor: SuspendTrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<Event<SearchScreenState>>()
    val screenState: LiveData<Event<SearchScreenState>> get() = _screenState

    private var searchText: String = ""
    private var lastSearchResults: List<Track> = emptyList()
    private var lastSearchQuery: String? = null
    private var searchJob: Job? = null

    private var isClickAllowed = true
    private var debounceJob: Job? = null

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun search(query: String, isRetry: Boolean = false) {
        if (!isRetry && query == lastSearchQuery) return

        searchText = query
        lastSearchQuery = query
        searchJob?.cancel()

        if (query.isEmpty()) {
            loadHistory()
        } else {
            searchJob = viewModelScope.launch {
                if (!isRetry) {
                    delay(SEARCH_DEBOUNCE_DELAY)
                }
                performSearch(searchText)
            }
        }
    }

    private suspend fun performSearch(term: String) {
        if (term.isEmpty()) {
            _screenState.postValue(Event(SearchScreenState.EmptyQuery))
            return
        }

        _screenState.postValue(Event(SearchScreenState.Loading))

        suspendTrackInteractor.search(term)
            .collect { result ->
                when (result) {
                    is NetworkResult.Success<List<Track>> -> {
                        lastSearchResults = result.data
                        val history = searchHistoryInteractor.getHistory()

                        if (result.data.isEmpty()) {
                            _screenState.postValue(Event(SearchScreenState.EmptyResult))
                        } else {
                            _screenState.postValue(
                                Event(
                                    SearchScreenState.Content(
                                        tracks = result.data,
                                        history = history,
                                        isHistoryVisible = false
                                    )
                                )
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _screenState.postValue(Event(SearchScreenState.Error))
                    }
                }
            }
    }

    fun loadHistory() {
        viewModelScope.launch {
            val history = searchHistoryInteractor.getHistory()
            _screenState.postValue(
                Event(
                    SearchScreenState.Content(
                        tracks = emptyList(),
                        history = history,
                        isHistoryVisible = true
                    )
                )
            )
        }
    }

    fun addTrackToHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.addTrack(track)
            loadHistory()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.clearHistory()
            loadHistory()
        }
    }

    fun getLastSearchResults(): List<Track> {
        return lastSearchResults
    }

    fun setLastSearchResults(results: List<Track>) {
        lastSearchResults = results
    }

    fun getLastSearchQuery(): String = lastSearchQuery ?: ""

    suspend fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            debounceJob?.cancel()
            debounceJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}