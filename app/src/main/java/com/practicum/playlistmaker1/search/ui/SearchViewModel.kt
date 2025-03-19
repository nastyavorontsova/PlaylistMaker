package com.practicum.playlistmaker1.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker1.app.Event
import com.practicum.playlistmaker1.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker1.search.domain.api.TrackInteractor
import com.practicum.playlistmaker1.search.domain.models.Track
import java.util.concurrent.Executors

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<Event<SearchScreenState>>()
    val screenState: LiveData<Event<SearchScreenState>> get() = _screenState

    private var searchText: String = ""
    private var lastSearchResults: List<Track> = emptyList()
    private var lastSearchQuery: String? = null // Храним последний запрос

    private val executor = Executors.newCachedThreadPool()
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val searchRunnable = Runnable {
        performSearch(searchText)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun search(query: String) {
        // Если запрос не изменился, не выполняем поиск
        if (query == lastSearchQuery) {
            return
        }

        searchText = query
        lastSearchQuery = query // Сохраняем последний запрос
        handler.removeCallbacks(searchRunnable)
        if (query.isEmpty()) {
            loadHistory()
        } else {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    private fun performSearch(term: String) {
        if (term.isEmpty()) {
            _screenState.value = Event(SearchScreenState.EmptyQuery)
            return
        }

        _screenState.value = Event(SearchScreenState.Loading)

        executor.execute {
            trackInteractor.search(term, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>) {
                    lastSearchResults = foundTracks
                    val history = searchHistoryInteractor.getHistory()
                    if (foundTracks.isNotEmpty()) {
                        _screenState.postValue(
                            Event(
                                SearchScreenState.Content(
                                    tracks = foundTracks,
                                    history = history,
                                    isHistoryVisible = false
                                )
                            )
                        )
                    } else {
                        _screenState.postValue(Event(SearchScreenState.EmptyResult))
                    }
                }
            }, object : TrackInteractor.ErrorConsumer {
                override fun onError() {
                    _screenState.postValue(Event(SearchScreenState.Error))
                }
            })
        }
    }

    fun loadHistory() {
        val history = searchHistoryInteractor.getHistory()
        _screenState.value = Event(
            SearchScreenState.Content(
                tracks = emptyList(),
                history = history,
                isHistoryVisible = true
            )
        )
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
        loadHistory()
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        loadHistory()
    }

    fun getLastSearchResults(): List<Track> {
        return lastSearchResults
    }

    fun setLastSearchResults(results: List<Track>) {
        lastSearchResults = results
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}