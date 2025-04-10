package com.practicum.playlistmaker1.search.data

import com.practicum.playlistmaker1.search.data.dto.NetworkResult
import com.practicum.playlistmaker1.search.data.dto.TrackDto
import com.practicum.playlistmaker1.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker1.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker1.search.domain.api.TrackRepository
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TrackRepository {

    override fun search(text: String): Flow<NetworkResult<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(text))
        val result = when (response) {
            is NetworkResult.Success -> {
                val tracks = response.data.results.map { it.toDomain() }
                NetworkResult.Success(tracks)
            }
            is NetworkResult.Error -> {
                response // Пробрасываем ошибку дальше
            }
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

    private fun TrackDto.toDomain(): Track = Track(
        trackId = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        trackTimeMillis = this.trackTimeMillis,
        previewUrl = this.previewUrl,
        artworkUrl100 = this.artworkUrl100
    )
}