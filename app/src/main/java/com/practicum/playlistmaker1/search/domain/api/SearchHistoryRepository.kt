package com.practicum.playlistmaker1.search.domain.api

import com.practicum.playlistmaker1.search.domain.models.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}