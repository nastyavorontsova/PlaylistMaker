package com.practicum.playlistmaker1.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker1.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker1.search.domain.api.TrackInteractor

class SearchViewModelFactory(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(trackInteractor, searchHistoryInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}