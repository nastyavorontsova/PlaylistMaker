package com.practicum.playlistmaker1.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class AudioPlayerViewModel(
    private var mediaPlayer: MediaPlayer
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _progress = MutableLiveData<String>()
    val progress: LiveData<String> get() = _progress

    private lateinit var track: Track

    private var progressUpdateJob: Job? = null

//    // Handler для обновления прогресса
//    private val handler = Handler(Looper.getMainLooper())
//    private val updateProgressRunnable = object : Runnable {
//        override fun run() {
//            updateProgress()
//            // Повторяем задачу каждые 500 мс
//            handler.postDelayed(this, 500)
//        }
//    }

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
            // Останавливаем обновление прогресса при завершении воспроизведения
            progressUpdateJob?.cancel()
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
        // Запускаем обновление прогресса
        startProgressUpdates()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        _playerState.value = PlayerState.PAUSED
        // Останавливаем обновление прогресса при паузе
        progressUpdateJob?.cancel()
    }

    private fun startProgressUpdates() {

        progressUpdateJob?.cancel()

        progressUpdateJob = viewModelScope.launch {
            while (isActive) {
                updateProgress()
                delay(300) // Обновляем каждые 300 мс
            }
        }
    }

    private fun updateProgress() {
        val currentPosition = mediaPlayer.currentPosition / 1000
        val minutes = currentPosition / 60
        val seconds = currentPosition % 60
        _progress.value = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        // Останавливаем Handler при очистке ViewModel
        progressUpdateJob?.cancel()
    }

    fun releaseMediaPlayer() {
        mediaPlayer.release()
    }

    enum class PlayerState {
        DEFAULT, PREPARED, PLAYING, PAUSED
    }
}