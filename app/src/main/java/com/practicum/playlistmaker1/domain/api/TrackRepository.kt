package com.practicum.playlistmaker1.domain.api

import com.practicum.playlistmaker1.domain.models.Track


interface TrackRepository {
    fun search(text: String): List<Track>
}