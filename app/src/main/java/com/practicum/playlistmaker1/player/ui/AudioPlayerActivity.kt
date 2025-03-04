package com.practicum.playlistmaker1.player.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker1.R
import com.practicum.playlistmaker1.search.domain.models.Track

import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel: AudioPlayerViewModel by viewModel<AudioPlayerViewModel>()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        track = intent.getParcelableExtra("TRACK_DATA") ?: return

        // Инициализация ViewModel с треком
        viewModel.initialize(track)

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
        setupPlayPauseButton()
        setupBackButton()

        viewModel.playerState.observe(this, { state ->
            when (state) {
                AudioPlayerViewModel.PlayerState.PLAYING -> playPauseButton.setImageResource(R.drawable.ic_pause)
                AudioPlayerViewModel.PlayerState.PAUSED, AudioPlayerViewModel.PlayerState.PREPARED -> playPauseButton.setImageResource(R.drawable.ic_play)
                else -> {}
            }
        })

        viewModel.progress.observe(this, { progress ->
            progressTextView.text = progress
        })
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

    private fun setupPlayPauseButton() {
        playPauseButton.setOnClickListener {
            viewModel.playPause()
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.playerState.value == AudioPlayerViewModel.PlayerState.PLAYING) {
            viewModel.playPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseMediaPlayer() // Освобождаем MediaPlayer при уничтожении Activity
    }
}