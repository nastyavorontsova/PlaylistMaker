package com.practicum.playlistmaker1.media.favorites.domain

import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTracksRepository {
    suspend fun addToFavourites(track: Track)
    suspend fun removeFromFavourites(track: Track)
    fun getAllFavouriteTracks(): Flow<List<Track>>
    fun getAllFavouriteTrackIds(): Flow<List<Long>>
}
