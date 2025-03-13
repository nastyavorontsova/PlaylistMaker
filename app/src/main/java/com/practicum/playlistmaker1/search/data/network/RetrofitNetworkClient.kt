package com.practicum.playlistmaker1.search.data.network

import com.practicum.playlistmaker1.search.data.NetworkClient
import com.practicum.playlistmaker1.search.data.dto.Response
import com.practicum.playlistmaker1.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient (private val itunesService: ApiService): NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            val resp = itunesService.search(dto.text).execute()

            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}