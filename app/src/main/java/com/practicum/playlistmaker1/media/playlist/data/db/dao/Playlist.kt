package com.practicum.playlistmaker1.media.playlist.data.db.dao

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<Long>,
    val tracksCount: Int
)