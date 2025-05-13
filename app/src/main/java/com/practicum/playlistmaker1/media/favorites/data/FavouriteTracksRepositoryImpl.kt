package com.practicum.playlistmaker1.media.favorites.data

import com.practicum.playlistmaker1.media.favorites.data.db.TrackDbConvertor
import com.practicum.playlistmaker1.media.favorites.data.db.dao.FavouriteTracksDao
import com.practicum.playlistmaker1.media.favorites.data.db.entity.FavouriteTrackEntity
import com.practicum.playlistmaker1.media.favorites.domain.FavouriteTracksRepository
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteTracksRepositoryImpl(
    private val dao: FavouriteTracksDao,
    private val converter: TrackDbConvertor
) : FavouriteTracksRepository {

    override suspend fun addToFavourites(track: Track) {
        dao.insertTrack(converter.fromDomainToEntity(track))
    }

    override suspend fun removeFromFavourites(track: Track) {
        dao.deleteTrack(converter.fromDomainToEntity(track))
    }

    override fun getAllFavouriteTracks(): Flow<List<Track>> {
        return dao.getAllFavouriteTracks()
            .map { tracks -> tracks.map { converter.fromEntityToDomain(it) } }
    }

    override fun getAllFavouriteTrackIds(): Flow<List<Long>> {
        return dao.getAllFavouriteTrackIds()
    }
}