package com.practicum.playlistmaker1.media.playlist.domain

import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist
import com.practicum.playlistmaker1.search.domain.models.Track

class PlaylistsInteractor(
    private val repository: PlaylistRepository
) {
    suspend fun getAllPlaylists(): List<Playlist> {
        return repository.getAllPlaylists()
    }

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }
}