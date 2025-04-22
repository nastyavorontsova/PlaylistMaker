package com.practicum.playlistmaker1.media.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_tracks")
data class FavouriteTrackEntity (
    @PrimaryKey
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
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val addedAt: Long = System.currentTimeMillis()
)