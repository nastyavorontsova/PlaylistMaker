package com.practicum.playlistmaker1.search.data.dto

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)
