package com.practicum.playlistmaker1.media.playlist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIdsJson: String, // Список ID треков в формате JSON
    val tracksCount: Int
)