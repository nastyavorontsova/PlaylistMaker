package com.practicum.playlistmaker1

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AudioPlayerActivity : AppCompatActivity() {

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

        setupBackButton()
    }

    private fun setupUI() {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        albumNameTextView.text = track.collectionName ?: "Неизвестный альбом"
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
}