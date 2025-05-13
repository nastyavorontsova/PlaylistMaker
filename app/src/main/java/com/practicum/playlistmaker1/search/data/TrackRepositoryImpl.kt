package com.practicum.playlistmaker1.search.data

import com.practicum.playlistmaker1.media.favorites.data.db.TrackDbConvertor
import com.practicum.playlistmaker1.media.favorites.data.db.dao.FavouriteTracksDao
import com.practicum.playlistmaker1.search.data.dto.NetworkResult
import com.practicum.playlistmaker1.search.data.dto.TrackDto
import com.practicum.playlistmaker1.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker1.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker1.search.domain.api.TrackRepository
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favouritesDao: FavouriteTracksDao,
    private val converter: TrackDbConvertor,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TrackRepository {

    override fun search(text: String): Flow<NetworkResult<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(text))
        val result = when (response) {
            is NetworkResult.Success -> {
                // Получаем ID избранных треков (первое значение из Flow)
                val favouriteIds = withContext(dispatcher) {
                    favouritesDao.getAllFavouriteTrackIds().first()
                }
                // Преобразуем DTO в Domain с учетом избранного статуса
                val tracks = response.data.results.map { dto ->
                    converter.fromDtoToDomain(dto).apply {
                        isFavorite = favouriteIds.contains(trackId)
                    }
                }
                NetworkResult.Success(tracks)
            }
            is NetworkResult.Error -> {
                response
            }
        }
        emit(result)
    }.flowOn(Dispatchers.IO)
}
