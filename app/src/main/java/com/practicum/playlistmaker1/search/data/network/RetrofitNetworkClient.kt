package com.practicum.playlistmaker1.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker1.search.data.NetworkClient
import com.practicum.playlistmaker1.search.data.dto.NetworkResult
import com.practicum.playlistmaker1.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker1.search.data.dto.TrackSearchResponse

class RetrofitNetworkClient(
    private val itunesService: ApiService,
    private val context: Context
) : NetworkClient {

    override suspend fun doRequest(dto: Any): NetworkResult<TrackSearchResponse> {
        if (!isConnected()) {
            return NetworkResult.Error(NetworkResult.Error.Companion.NO_INTERNET)
        }

        if (dto !is TrackSearchRequest) {
            return NetworkResult.Error(NetworkResult.Error.Companion.BAD_REQUEST)
        }

        return try {
            val response = itunesService.search(dto.text)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Error(NetworkResult.Error.Companion.SERVER_ERROR)
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }
}