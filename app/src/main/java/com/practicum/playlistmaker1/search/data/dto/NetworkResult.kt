package com.practicum.playlistmaker1.search.data.dto

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val errorCode: Int) : NetworkResult<Nothing>() {
        companion object {
            const val NO_INTERNET = -1
            const val BAD_REQUEST = 400
            const val SERVER_ERROR = 500
        }
    }
}