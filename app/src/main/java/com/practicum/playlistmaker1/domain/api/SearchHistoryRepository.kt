package com.practicum.playlistmaker1.domain.api

import com.practicum.playlistmaker1.domain.models.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}