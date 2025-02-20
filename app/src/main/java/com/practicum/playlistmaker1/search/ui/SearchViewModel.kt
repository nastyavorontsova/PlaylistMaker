package com.practicum.playlistmaker1.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker1.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker1.search.domain.api.TrackInteractor
import com.practicum.playlistmaker1.search.domain.models.Track
import java.util.concurrent.Executors

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    // LiveData для состояния поиска
    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> get() = _searchState

    // LiveData для истории поиска
    private val _historyState = MutableLiveData<List<Track>>()
    val historyState: LiveData<List<Track>> get() = _historyState

    // Переменная для хранения текущего запроса
    private var searchText: String = ""

    // Executor для выполнения запросов в фоновом потоке
    private val executor = Executors.newCachedThreadPool()

    // Handler для debounce
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val searchRunnable = Runnable {
        performSearch(searchText)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    // Метод для запуска поиска с debounce
    fun search(query: String) {
        searchText = query
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    // Метод для выполнения поиска
    private fun performSearch(term: String) {
        if (term.isEmpty()) {
            _searchState.value = SearchState.EmptyQuery
            return
        }

        _searchState.value = SearchState.Loading

        executor.execute {
            trackInteractor.search(term, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>) {
                    if (foundTracks.isNotEmpty()) {
                        _searchState.postValue(SearchState.Success(foundTracks))
                    } else {
                        _searchState.postValue(SearchState.EmptyResult)
                    }
                }
            }, object : TrackInteractor.ErrorConsumer {
                override fun onError() {
                    _searchState.postValue(SearchState.Error)
                }
            })
        }
    }

    // Метод для загрузки истории поиска
    fun loadHistory() {
        val history = searchHistoryInteractor.getHistory()
        _historyState.value = history
    }

    // Метод для добавления трека в историю
    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
        loadHistory()
    }

    // Метод для очистки истории
    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        loadHistory()
    }

    // Состояния поиска
    sealed class SearchState {
        object Loading : SearchState()
        object EmptyQuery : SearchState()
        object EmptyResult : SearchState()
        object Error : SearchState()
        data class Success(val tracks: List<Track>) : SearchState()
    }

    // Очистка ресурсов при уничтожении ViewModel
    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}