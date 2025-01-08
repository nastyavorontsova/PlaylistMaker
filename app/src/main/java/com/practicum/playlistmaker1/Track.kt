package com.practicum.playlistmaker1

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
    val artworkUrl100: String
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
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }

    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }
}

