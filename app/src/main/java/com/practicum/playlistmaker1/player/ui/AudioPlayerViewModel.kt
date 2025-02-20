package com.practicum.playlistmaker1.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker1.search.domain.models.Track
import java.util.concurrent.Executors

class AudioPlayerViewModel : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _progress = MutableLiveData<String>()
    val progress: LiveData<String> get() = _progress

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var track: Track
    private val executor = Executors.newSingleThreadExecutor()

    fun initialize(track: Track) {
        this.track = track
        mediaPlayer = MediaPlayer()
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _playerState.value = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            _playerState.value = PlayerState.PREPARED
            _progress.value = "00:00"
        }
    }

    fun playPause() {
        when (_playerState.value) {
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.PLAYING -> pausePlayer()
            else -> {

                mediaPlayer.setOnPreparedListener {
                    _playerState.value = PlayerState.PREPARED
                    startPlayer()
                }
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        _playerState.value = PlayerState.PLAYING
        updateProgress()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        _playerState.value = PlayerState.PAUSED
    }

    private fun updateProgress() {
        executor.execute {
            while (mediaPlayer.isPlaying) {
                val currentPosition = mediaPlayer.currentPosition / 1000
                val minutes = currentPosition / 60
                val seconds = currentPosition % 60
                _progress.postValue(String.format("%02d:%02d", minutes, seconds))
                Thread.sleep(500)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        executor.shutdown()
    }

    enum class PlayerState {
        DEFAULT, PREPARED, PLAYING, PAUSED
    }
}