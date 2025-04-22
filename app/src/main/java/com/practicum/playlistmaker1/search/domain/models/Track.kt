package com.practicum.playlistmaker1.search.domain.models

import android.icu.text.SimpleDateFormat
import android.os.Parcel
import android.os.Parcelable
import java.util.Locale

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

    constructor(parcel: Parcel) : this(
        trackId = parcel.readLong(),
        trackName = parcel.readString() ?: "",
        artistName = parcel.readString() ?: "",
        collectionName = parcel.readString(),
        releaseDate = parcel.readString() ?: "",
        primaryGenreName = parcel.readString() ?: "",
        country = parcel.readString() ?: "",
        artworkUrl100 = parcel.readString() ?: "",
        trackTimeMillis = parcel.readLong(),
        previewUrl = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(trackId)
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(collectionName)
        parcel.writeString(releaseDate)
        parcel.writeString(primaryGenreName)
        parcel.writeString(country)
        parcel.writeString(artworkUrl100)
        parcel.writeLong(trackTimeMillis)
        parcel.writeString(previewUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }

    fun getFormattedDuration(): String {
        val minutes = (trackTimeMillis / 1000) / 60
        val seconds = (trackTimeMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getCoverArtwork(): String {
        return artworkUrl100.replace("100x100bb", "500x500bb")
    }
}

