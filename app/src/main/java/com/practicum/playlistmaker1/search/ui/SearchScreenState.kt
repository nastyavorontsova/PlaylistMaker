package com.practicum.playlistmaker1.search.ui

import com.practicum.playlistmaker1.search.domain.models.Track

sealed class SearchScreenState {
    data class Content(
        val tracks: List<Track>, // Список найденных треков
        val history: List<Track>, // Список истории поиска
        val isHistoryVisible: Boolean // Видимость истории
    ) : SearchScreenState()

    object Loading : SearchScreenState()
    object EmptyQuery : SearchScreenState()
    object EmptyResult : SearchScreenState()
    object Error : SearchScreenState()
}