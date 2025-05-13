package com.practicum.playlistmaker1.media.playlist.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.media.playlist.domain.PlaylistRepository
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _playlistCreated = MutableLiveData<Boolean>()
    val playlistCreated: LiveData<Boolean> = _playlistCreated

    private val _showExitDialog = MutableLiveData<Boolean>()
    val showExitDialog: LiveData<Boolean> = _showExitDialog

    fun createPlaylist(name: String, description: String?, coverUri: Uri?) {
        viewModelScope.launch {
            try {
                playlistRepository.createPlaylist(name, description, coverUri)
                _playlistCreated.postValue(true)
            } catch (e: Exception) {
                _playlistCreated.postValue(false)
            }
        }
    }

    fun checkForUnsavedChanges(hasData: Boolean) {
        _showExitDialog.value = hasData
    }
}