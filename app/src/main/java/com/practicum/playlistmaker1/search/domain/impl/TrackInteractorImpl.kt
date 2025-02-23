package com.practicum.playlistmaker1.search.domain.impl

import com.practicum.playlistmaker1.search.domain.api.TrackInteractor
import com.practicum.playlistmaker1.search.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun search(
        text: String,
        consumer: TrackInteractor.TrackConsumer,
        errorConsumer: TrackInteractor.ErrorConsumer
    ) {
        executor.execute {
            try {
                val tracks = repository.search(text)
                consumer.consume(tracks)
            } catch (e: Exception) {
                errorConsumer.onError()
            }
        }
    }
}
