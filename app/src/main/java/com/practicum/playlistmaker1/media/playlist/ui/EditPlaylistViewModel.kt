package com.practicum.playlistmaker1.media.playlist.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.media.playlist.data.db.PlaylistRepositoryException
import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist
import com.practicum.playlistmaker1.media.playlist.domain.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditPlaylistViewModel(
    application: Application,
    playlistRepository: PlaylistRepository
) : PlaylistViewModel(playlistRepository, application) {

    private val _playlistData = MutableLiveData<Playlist?>()
    val playlistData: LiveData<Playlist?> = _playlistData

    private val _playlistUpdated = MutableLiveData<Boolean>()
    val playlistUpdated: LiveData<Boolean> = _playlistUpdated

    private val _newCoverUri = MutableLiveData<Uri?>()
    val newCoverUri: LiveData<Uri?> get() = _newCoverUri

    fun setNewCoverUri(uri: Uri) {
        _newCoverUri.value = uri
    }

    fun loadPlaylistById(id: Long) {
        // Например, асинхронно загружаем плейлист из репозитория
        viewModelScope.launch {
            val playlist = playlistRepository.getPlaylistById(id)
            _playlistData.postValue(playlist)
        }
    }

    fun updatePlaylist(name: String, description: String?, coverUri: Uri?) {
        val currentPlaylist = _playlistData.value ?: return
        val context = getApplication<Application>()

        viewModelScope.launch {
            val savedCoverPath = coverUri?.let {
                saveCoverToInternalStorage(context, it)
            } ?: currentPlaylist.coverPath

            val updatedPlaylist = currentPlaylist.copy(
                name = name,
                description = description,
                coverPath = savedCoverPath
            )
            playlistRepository.updatePlaylist(updatedPlaylist)
            _playlistUpdated.postValue(true)
        }
    }

    private suspend fun saveCoverToInternalStorage(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "playlist_cover_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            Log.e("EditPlaylistVM", "Failed to save cover", e)
            null
        }
    }
}

