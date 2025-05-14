package com.practicum.playlistmaker1.player.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.app.Event
import com.practicum.playlistmaker1.media.favorites.domain.FavouriteTracksInteractor
import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist
import com.practicum.playlistmaker1.media.playlist.domain.PlaylistsInteractor
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val interactor: FavouriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private val _playerState = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _progress = MutableLiveData<String>("00:00")
    val progress: LiveData<String> get() = _progress

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private lateinit var track: Track
    private var progressUpdateJob: Job? = null
    private var isInitialized = false

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _addToPlaylistStatus = MutableLiveData<Event<AddToPlaylistStatus>>()
    val addToPlaylistStatus: LiveData<Event<AddToPlaylistStatus>> get() = _addToPlaylistStatus


    init {
        savedStateHandle.get<Track>("TRACK_DATA")?.let { track ->
            initialize(track)
        }
    }

    fun initialize(track: Track) {
        if (::track.isInitialized && this.track.trackId == track.trackId) return

        this.track = track
        isInitialized = false
        viewModelScope.launch {
            _isFavorite.value = interactor.isTrackFavourite(track.trackId)
            track.isFavorite = _isFavorite.value == true
        }
        resetMediaPlayer()
    }

    private fun resetMediaPlayer() {
        progressUpdateJob?.cancel()
        mediaPlayer?.release()
        _playerState.value = PlayerState.DEFAULT
        _progress.value = "00:00"
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            try {
                setDataSource(track.previewUrl)
                prepareAsync()
                setOnPreparedListener {
                    _playerState.postValue(PlayerState.PREPARED)
                    isInitialized = true
                }
                setOnCompletionListener {
                    _playerState.postValue(PlayerState.PREPARED)
                    _progress.postValue("00:00")
                    progressUpdateJob?.cancel()
                }
            } catch (e: Exception) {
                _playerState.postValue(PlayerState.DEFAULT)
                _progress.postValue("00:00")
            }
        }
    }

    fun playPause() {
        if (!isInitialized) {
            mediaPlayer?.setOnPreparedListener {
                isInitialized = true
                startPlayer()
            }
            return
        }

        when (_playerState.value) {
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.PLAYING -> pausePlayer()
            else -> Unit
        }
    }

    private fun startPlayer() {
        mediaPlayer?.let {
            it.start()
            _playerState.value = PlayerState.PLAYING
            startProgressUpdates()
        }
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        _playerState.value = PlayerState.PAUSED
        progressUpdateJob?.cancel()
    }

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (isActive) {
                updateProgress()
                delay(100)
            }
        }
    }

    private fun updateProgress() {
        mediaPlayer?.let {
            val currentPosition = it.currentPosition
            val minutes = (currentPosition / 1000) / 60
            val seconds = (currentPosition / 1000) % 60
            _progress.postValue(String.format("%02d:%02d", minutes, seconds))
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val currentlyFavorite = _isFavorite.value == true
            if (currentlyFavorite) {
                interactor.removeFromFavourites(track)
            } else {
                interactor.addToFavourites(track)
            }
            _isFavorite.postValue(!currentlyFavorite)
            track.isFavorite = !currentlyFavorite
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            _playlists.value = playlistsInteractor.getAllPlaylists()
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            if (playlist.trackIds.contains(track.trackId)) {
                _addToPlaylistStatus.value = Event(AddToPlaylistStatus.AlreadyExists(playlist.name))
            } else {
                playlistsInteractor.addTrackToPlaylist(track, playlist)
                _addToPlaylistStatus.value = Event(AddToPlaylistStatus.Success(playlist.name))
            }
        }
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        progressUpdateJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        releaseMediaPlayer()
    }

    enum class PlayerState {
        DEFAULT, PREPARED, PLAYING, PAUSED
    }

    sealed class AddToPlaylistStatus {
        data class Success(val playlistName: String) : AddToPlaylistStatus()
        data class AlreadyExists(val playlistName: String) : AddToPlaylistStatus()
    }
}