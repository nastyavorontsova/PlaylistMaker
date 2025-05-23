package com.practicum.playlistmaker1.media.playlist.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.app.Event
import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist
import com.practicum.playlistmaker1.media.playlist.domain.PlaylistsInteractor
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.launch

class CreatedPlaylistViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    private val _playlistData = MutableLiveData<Pair<Playlist, List<Track>>?>()
    val playlistData: LiveData<Pair<Playlist, List<Track>>?> = _playlistData

    private val _showShareDialog = MutableLiveData<Event<String>>()
    val showShareDialog: LiveData<Event<String>> = _showShareDialog

    private val _showEmptyPlaylistToast = MutableLiveData<Event<Unit>>()
    val showEmptyPlaylistToast: LiveData<Event<Unit>> = _showEmptyPlaylistToast

    private val _navigateToMediaLibrary = MutableLiveData<Event<Unit>>()
    val navigateToMediaLibrary: LiveData<Event<Unit>> = _navigateToMediaLibrary

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentPlaylistId: Long = -1L

    fun loadPlaylist(playlistId: Long) {
        currentPlaylistId = playlistId
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _playlistData.value = interactor.getPlaylistWithTracks(playlistId)
            } catch (e: Exception) {
                Log.e("CreatedPlaylistVM", "Error loading playlist", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sharePlaylist() {
        val currentData = _playlistData.value
        if (currentData?.second?.isEmpty() != false) {
            _showEmptyPlaylistToast.value = Event(Unit)
        } else {
            val (playlist, tracks) = currentData
            val shareText = buildShareText(playlist, tracks)
            _showShareDialog.value = Event(shareText)
        }
    }

    private fun buildShareText(playlist: Playlist, tracks: List<Track>): String {
        return buildString {
            append(playlist.name)
            playlist.description?.let { desc ->
                append("\n").append(desc)
            }
            append("\n${playlist.tracksCount} треков")
            append("\n\n")
            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.getFormattedDuration()})\n")
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            interactor.deletePlaylist(currentPlaylistId)
            _navigateToMediaLibrary.value = Event(Unit)
        }
    }

    fun removeTrack(trackId: Long) {
        viewModelScope.launch {
            try {
                interactor.removeTrackFromPlaylist(currentPlaylistId, trackId)
                loadPlaylist(currentPlaylistId) // Обновляем данные
            } catch (e: Exception) {
                Log.e("CreatedPlaylistVM", "Error removing track", e)
            }
        }
    }
}