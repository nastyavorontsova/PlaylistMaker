package com.practicum.playlistmaker1.search.data

import com.practicum.playlistmaker1.search.data.dto.NetworkResult
import com.practicum.playlistmaker1.search.data.dto.TrackSearchResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): NetworkResult<TrackSearchResponse>
}