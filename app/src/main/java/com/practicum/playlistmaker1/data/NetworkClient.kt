package com.practicum.playlistmaker1.data

import com.practicum.playlistmaker1.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}
