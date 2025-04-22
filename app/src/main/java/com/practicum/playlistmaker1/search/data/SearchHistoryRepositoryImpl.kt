package com.practicum.playlistmaker1.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker1.media.data.db.TrackDbConvertor
import com.practicum.playlistmaker1.media.data.db.dao.FavouriteTracksDao
import com.practicum.playlistmaker1.search.data.dto.TrackDto
import com.practicum.playlistmaker1.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val favouritesDao: FavouriteTracksDao,
    private val converter: TrackDbConvertor,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : SearchHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()

    override suspend fun getHistory(): List<Track> = withContext(dispatcher) {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
        if (!json.isNullOrEmpty()) {
            val historyDtos = gson.fromJson(json, Array<TrackDto>::class.java).toList()
            val favouriteIds = favouritesDao.getAllFavouriteTrackIds().first() // Получаем первый элемент Flow

            historyDtos.map { dto ->
                converter.fromDtoToDomain(dto).apply {
                    isFavorite = favouriteIds.contains(trackId)
                }
            }
        } else {
            emptyList()
        }
    }

    override suspend fun addTrack(track: Track) = withContext(dispatcher) {
        val history = getHistory().map { converter.fromDomainToDto(it) }.toMutableList()
        val trackDto = converter.fromDomainToDto(track)

        history.remove(trackDto)
        history.add(0, trackDto)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<TrackDto>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }
}