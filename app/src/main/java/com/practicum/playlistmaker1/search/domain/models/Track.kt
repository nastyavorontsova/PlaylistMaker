package com.practicum.playlistmaker1.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val trackTimeMillis: Long,
    val previewUrl: String,
    val artworkUrl100: String,
    var isFavorite: Boolean = false
) : Parcelable {

    fun getFormattedDuration(): String {
        val minutes = (trackTimeMillis / 1000) / 60
        val seconds = (trackTimeMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getCoverArtwork(): String {
        return artworkUrl100.replace("100x100bb", "500x500bb")
    }
}
