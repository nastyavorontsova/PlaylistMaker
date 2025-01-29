package com.practicum.playlistmaker1.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker1.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker1.domain.models.Track

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SearchHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            gson.fromJson(json, Array<Track>::class.java).toList()
        } else {
            emptyList()
        }
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.remove(track)
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }
}
