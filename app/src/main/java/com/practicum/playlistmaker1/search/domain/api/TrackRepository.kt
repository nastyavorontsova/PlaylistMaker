package com.practicum.playlistmaker1.search.domain.api

import com.practicum.playlistmaker1.search.domain.models.Track


interface TrackRepository {
    fun search(text: String): List<Track>
}