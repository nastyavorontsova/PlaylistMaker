package com.practicum.playlistmaker1.search.domain.api

import com.practicum.playlistmaker1.search.data.dto.NetworkResult
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SuspendTrackInteractor {
    fun search(text: String): Flow<NetworkResult<List<Track>>>
}