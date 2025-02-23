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

    // LiveData для состояния экрана
    private val _screenState = MutableLiveData<SearchScreenState>()
    val screenState: LiveData<SearchScreenState> get() = _screenState

    // Переменная для хранения текущего запроса
    private var searchText: String = ""

    // Переменная для хранения последних результатов поиска
    private var lastSearchResults: List<Track> = emptyList()

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
        if (query.isEmpty()) {
            loadHistory() // Загружаем историю при пустом запросе
        } else {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    // Метод для выполнения поиска
    private fun performSearch(term: String) {
        if (term.isEmpty()) {
            _screenState.value = SearchScreenState.EmptyQuery
            return
        }

        _screenState.value = SearchScreenState.Loading

        executor.execute {
            trackInteractor.search(term, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>) {
                    lastSearchResults = foundTracks
                    val history = searchHistoryInteractor.getHistory()
                    if (foundTracks.isNotEmpty()) {
                        _screenState.postValue(
                            SearchScreenState.Content(
                                tracks = foundTracks,
                                history = history,
                                isHistoryVisible = false // История скрыта при успешном поиске
                            )
                        )
                    } else {
                        _screenState.postValue(SearchScreenState.EmptyResult)
                    }
                }
            }, object : TrackInteractor.ErrorConsumer {
                override fun onError() {
                    _screenState.postValue(SearchScreenState.Error)
                }
            })
        }
    }

    // Метод для загрузки истории поиска
    fun loadHistory() {
        val history = searchHistoryInteractor.getHistory()
        _screenState.value = SearchScreenState.Content(
            tracks = emptyList(), // Пустой список треков
            history = history,
            isHistoryVisible = true // История видима
        )
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

    fun getLastSearchResults(): List<Track> {
        return lastSearchResults
    }

    // Очистка ресурсов при уничтожении ViewModel
    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}
