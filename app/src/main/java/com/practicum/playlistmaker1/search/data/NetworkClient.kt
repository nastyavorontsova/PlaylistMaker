package com.practicum.playlistmaker1.search.data

import com.practicum.playlistmaker1.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}
