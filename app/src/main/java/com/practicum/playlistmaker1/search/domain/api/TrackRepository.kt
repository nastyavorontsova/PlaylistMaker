package com.practicum.playlistmaker1.search.domain.api

import com.bumptech.glide.load.engine.Resource
import com.practicum.playlistmaker1.search.data.dto.NetworkResult
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun search(text: String): Flow<NetworkResult<List<Track>>>
}