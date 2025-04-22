package com.practicum.playlistmaker1.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.media.domain.FavouriteTracksInteractor
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val interactor: FavouriteTracksInteractor
) : ViewModel() {

    sealed class State {
        object Empty : State()
        data class Content(val tracks: List<Track>) : State()
    }

    private val _state = MutableLiveData<State>(State.Empty)
    val state: LiveData<State> get() = _state

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            interactor.getAllFavouriteTracks()
                .collect { tracks ->
                    _state.postValue(
                        if (tracks.isEmpty()) State.Empty
                        else State.Content(tracks.sortedByDescending { it.isFavorite })
                    )
                }
        }
    }
}
