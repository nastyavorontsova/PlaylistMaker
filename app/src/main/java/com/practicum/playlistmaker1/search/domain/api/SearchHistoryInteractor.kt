package com.practicum.playlistmaker1.search.domain.api

import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchHistoryInteractor(
    private val repository: SearchHistoryRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getHistory(): List<Track> {
        return withContext(dispatcher) {
            repository.getHistory()
        }
    }

    suspend fun addTrack(track: Track) {
        withContext(dispatcher) {
            repository.addTrack(track)
        }
    }

    fun clearHistory() {
        repository.clearHistory()
    }
}