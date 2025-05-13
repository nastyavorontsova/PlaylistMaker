package com.practicum.playlistmaker1.media.playlist.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist
import com.practicum.playlistmaker1.media.playlist.domain.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                _playlists.value = interactor.getAllPlaylists()
            } catch (e: Exception) {
                Log.e("PlaylistsViewModel", "Error loading playlists", e)
            }
        }
    }
}