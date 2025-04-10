package com.practicum.playlistmaker1.search.domain.impl

import com.practicum.playlistmaker1.search.data.dto.NetworkResult
import com.practicum.playlistmaker1.search.domain.api.SuspendTrackInteractor
import com.practicum.playlistmaker1.search.domain.api.TrackRepository
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class SuspendTrackInteractorImpl(
    private val trackRepository: TrackRepository
) : SuspendTrackInteractor {
    override fun search(text: String): Flow<NetworkResult<List<Track>>> {
        return trackRepository.search(text)
    }
}