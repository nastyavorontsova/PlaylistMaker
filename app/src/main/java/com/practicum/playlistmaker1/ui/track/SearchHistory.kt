package com.practicum.playlistmaker1.ui.track

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker1.domain.models.Track

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val  gson = Gson()
    private val historyKey = "search_history"

    fun getHistory(): MutableList<Track> {
        val json = sharedPreferences.getString(historyKey, null)

        return if (json != null) {
            val type = object : TypeToken<MutableList<Track>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }
    }

    fun addTrack(track: Track) {
        val history = getHistory()

        history.removeIf { it.trackId == track.trackId }

        history.add(0, track)

        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }

        saveHistory(history)
    }

    private fun saveHistory(history: MutableList<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit()
            .putString(historyKey, json)
            .apply()
    }

    fun clearHistory() {
        sharedPreferences.edit()
            .remove(historyKey)
            .apply()
    }
}