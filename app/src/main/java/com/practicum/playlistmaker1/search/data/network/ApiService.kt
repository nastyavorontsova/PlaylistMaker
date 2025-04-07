package com.practicum.playlistmaker1.search.data.network


import com.practicum.playlistmaker1.search.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): TrackSearchResponse
}