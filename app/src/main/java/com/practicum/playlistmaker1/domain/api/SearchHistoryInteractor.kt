package com.practicum.playlistmaker1.domain.api

import com.practicum.playlistmaker1.domain.models.Track

class SearchHistoryInteractor(private val repository: SearchHistoryRepository) {

    fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    fun clearHistory() {
        repository.clearHistory()
    }
}