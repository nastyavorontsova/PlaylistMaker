package com.practicum.playlistmaker1.search.domain.api

import com.practicum.playlistmaker1.search.domain.models.Track

interface TrackInteractor {
    fun search(
        text: String,
        consumer: TrackConsumer,
        errorConsumer: ErrorConsumer
    )

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
    }

    interface ErrorConsumer {
        fun onError()
    }
}