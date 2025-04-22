package com.practicum.playlistmaker1.player.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.media.domain.FavouriteTracksInteractor
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val interactor: FavouriteTracksInteractor
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

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        progressUpdateJob?.cancel()
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        progressUpdateJob?.cancel()
    }

    enum class PlayerState {
        DEFAULT, PREPARED, PLAYING, PAUSED
    }
}