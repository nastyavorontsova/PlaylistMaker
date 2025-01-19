package com.practicum.playlistmaker1

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AudioPlayerActivity : AppCompatActivity() {

    private companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    private lateinit var track: Track
    private lateinit var coverArt: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var albumNameTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var trackDurationTextView: TextView
    private lateinit var addToPlaylistButton: ImageView
    private lateinit var addToFavoritesButton: ImageView
    private lateinit var playPauseButton: ImageView
    private lateinit var progressTextView: TextView

    private var mediaPlayer = MediaPlayer()
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        track = intent.getParcelableExtra("TRACK_DATA") ?: return

        coverArt = findViewById(R.id.coverArt)
        trackNameTextView = findViewById(R.id.songName)
        artistNameTextView = findViewById(R.id.artistName)
        albumNameTextView = findViewById(R.id.albumName)
        releaseDateTextView = findViewById(R.id.releaseDate)
        genreTextView = findViewById(R.id.genre)
        countryTextView = findViewById(R.id.country)
        trackDurationTextView = findViewById(R.id.trackDuration)
        addToPlaylistButton = findViewById(R.id.addToPlaylistButton)
        addToFavoritesButton = findViewById(R.id.addToFavoritesButton)
        playPauseButton = findViewById(R.id.playPauseButton)
        progressTextView = findViewById(R.id.progress)

        setupUI()
        preparePlayer()
        setupPlayPauseButton()
        setupBackButton()

        handler = Handler(Looper.getMainLooper())
    }

    private fun setupUI() {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        albumNameTextView.text = track.collectionName ?: getString(R.string.unknown_album)
        releaseDateTextView.text = track.releaseDate
        genreTextView.text = track.primaryGenreName
        countryTextView.text = track.country
        trackDurationTextView.text = formatTrackDuration(track.trackTimeMillis)

        val cornerRadius = this.resources.getDimensionPixelSize(R.dimen.cover_art_radius)

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_cover)
            .transform(RoundedCorners(cornerRadius))
            .into(coverArt)
    }

    private fun formatTrackDuration(duration: Long): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupBackButton() {
        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            resetPlayer()
        }
    }

    private fun setupPlayPauseButton() {
        playPauseButton.setOnClickListener {
            when (playerState) {
                STATE_PREPARED, STATE_PAUSED -> startPlayer()
                STATE_PLAYING -> pausePlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playPauseButton.setImageResource(R.drawable.ic_pause)
        updateProgress()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playPauseButton.setImageResource(R.drawable.ic_play)
        handler.removeCallbacksAndMessages(null)
    }

    private fun resetPlayer() {
        playerState = STATE_PREPARED
        playPauseButton.setImageResource(R.drawable.ic_play)
        handler.removeCallbacksAndMessages(null)
        progressTextView.text = "00:00"
    }

    private fun updateProgress() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    val currentPosition = mediaPlayer.currentPosition / 1000
                    val minutes = currentPosition / 60
                    val seconds = currentPosition % 60
                    progressTextView.text = String.format("%02d:%02d", minutes, seconds)
                    handler.postDelayed(this, 500)
                }
            }
        }, 500)
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }
}