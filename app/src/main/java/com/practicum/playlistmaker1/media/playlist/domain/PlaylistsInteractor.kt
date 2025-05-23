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

    suspend fun getPlaylistWithTracks(playlistId: Long): Pair<Playlist, List<Track>>? {
        return repository.getPlaylistWithTracks(playlistId)
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        repository.removeTrackFromPlaylist(playlistId, trackId)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }
}