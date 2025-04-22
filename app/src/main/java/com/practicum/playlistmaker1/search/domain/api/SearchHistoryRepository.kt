package com.practicum.playlistmaker1.search.domain.api

import com.practicum.playlistmaker1.search.domain.models.Track

interface SearchHistoryRepository {
    suspend fun getHistory(): List<Track>
    suspend fun addTrack(track: Track)
    fun clearHistory()
}