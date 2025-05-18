package com.practicum.playlistmaker1.media.playlist.domain

import android.net.Uri
import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist
import com.practicum.playlistmaker1.search.domain.models.Track

interface PlaylistRepository {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverUri: Uri?
    ): Long

    suspend fun getAllPlaylists(): List<Playlist>

    suspend fun getPlaylistById(playlistId: Long): Playlist?

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
}