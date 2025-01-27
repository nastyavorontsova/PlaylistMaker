package com.practicum.playlistmaker1.domain.impl

import com.practicum.playlistmaker1.domain.api.TrackInteractor
import com.practicum.playlistmaker1.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl (private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun search(
        text: String,
        consumer: TrackInteractor.TrackConsumer,
        errorConsumer: TrackInteractor.ErrorConsumer
    ) {
        executor.execute {
            try {
                // Выполняем запрос в фоновом потоке
                val tracks = repository.search(text)
                consumer.consume(tracks)
            } catch (e: Exception) {
                // В случае ошибки вызываем errorConsumer
                errorConsumer.onError()
            }
        }
    }
}
