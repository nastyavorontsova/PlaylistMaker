package com.practicum.playlistmaker1.media.domain

import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FavouriteTracksInteractor(
    private val repository: FavouriteTracksRepository
) {
    private var cachedFavouriteIds: List<Long> = emptyList()

    suspend fun addToFavourites(track: Track) {
        repository.addToFavourites(track)
        cachedFavouriteIds = repository.getAllFavouriteTrackIds().first()
    }

    suspend fun removeFromFavourites(track: Track) {
        repository.removeFromFavourites(track)
        cachedFavouriteIds = repository.getAllFavouriteTrackIds().first()
    }

    fun getAllFavouriteTracks(): Flow<List<Track>> = repository.getAllFavouriteTracks()

    suspend fun isTrackFavourite(trackId: Long): Boolean {
        if (cachedFavouriteIds.isEmpty()) {
            cachedFavouriteIds = repository.getAllFavouriteTrackIds().first()
        }
        return cachedFavouriteIds.contains(trackId)
    }

    suspend fun getAllFavouriteTrackIds(): List<Long> {
        return repository.getAllFavouriteTrackIds().first()
    }
}