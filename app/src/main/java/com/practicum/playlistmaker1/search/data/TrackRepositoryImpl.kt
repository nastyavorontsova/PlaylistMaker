package com.practicum.playlistmaker1.search.data

import com.practicum.playlistmaker1.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker1.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker1.search.domain.api.TrackRepository
import com.practicum.playlistmaker1.search.domain.models.Track

class TrackRepositoryImpl (private val networkClient: NetworkClient) : TrackRepository {

    override fun search(text: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(text))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
                Track(it.trackId, it.trackName, it.artistName, it.collectionName, it.releaseDate, it.primaryGenreName, it.country, it.trackTimeMillis, it.previewUrl, it.artworkUrl100) }
        } else {
            return emptyList()
        }
    }
}
