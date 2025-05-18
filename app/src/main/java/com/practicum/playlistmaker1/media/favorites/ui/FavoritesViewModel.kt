package com.practicum.playlistmaker1.media.favorites.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.media.favorites.domain.FavouriteTracksInteractor
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val interactor: FavouriteTracksInteractor
) : ViewModel() {


    private var isClickAllowed = true
    private var debounceJob: Job? = null

    sealed class State {
        object Empty : State()
        data class Content(val tracks: List<Track>) : State()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
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

    fun canClick(): Boolean {
        val allowed = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            debounceJob?.cancel()
            debounceJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return allowed
    }
}
