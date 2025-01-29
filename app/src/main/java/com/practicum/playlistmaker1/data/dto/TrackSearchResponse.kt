package com.practicum.playlistmaker1.data.dto

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()
