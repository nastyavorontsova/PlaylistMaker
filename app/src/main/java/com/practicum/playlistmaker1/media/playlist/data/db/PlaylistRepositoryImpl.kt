package com.practicum.playlistmaker1.media.playlist.data.db

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker1.media.playlist.data.db.dao.Playlist
import com.practicum.playlistmaker1.media.playlist.data.db.dao.PlaylistDao
import com.practicum.playlistmaker1.media.playlist.data.db.dao.PlaylistTracksDao
import com.practicum.playlistmaker1.media.playlist.data.db.dao.toPlaylistTrackEntity
import com.practicum.playlistmaker1.media.playlist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker1.media.playlist.domain.PlaylistRepository
import com.practicum.playlistmaker1.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val tracksDao: PlaylistTracksDao,
    private val context: Context,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverUri: Uri?
    ): Long {
        return try {
            // Сохраняем обложку во внутреннее хранилище
            val coverPath = coverUri?.let { saveCoverToInternalStorage(it) }

            // Создаем новую сущность плейлиста
            val newPlaylist = PlaylistEntity(
                name = name,
                description = description,
                coverPath = coverPath,
                trackIdsJson = gson.toJson(emptyList<Long>()), // Пустой список треков
                tracksCount = 0
            )

            // Вставляем в БД и возвращаем ID
            playlistDao.insert(newPlaylist)
        } catch (e: Exception) {
            throw PlaylistRepositoryException("Failed to create playlist", e)
        }
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playlistDao.getAllPlaylists().map { it.toDomain() }
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistDao.getPlaylistById(playlistId)?.toDomain()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.update(playlist.toEntity(gson))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.getPlaylistById(playlistId)?.let { playlist ->
            // Удаляем файл обложки, если он есть
            playlist.coverPath?.let { path ->
                File(path).delete()
            }
            playlistDao.delete(playlist)
        }
    }

    private suspend fun saveCoverToInternalStorage(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IOException("Cannot open input stream")

                val fileName = "cover_${System.currentTimeMillis()}.jpg"
                val outputFile = File(context.filesDir, fileName)

                inputStream.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }

                return@withContext outputFile.absolutePath
            } catch (e: Exception) {
                throw PlaylistRepositoryException("Failed to save cover image", e)
            }
        }
    }

    private fun PlaylistEntity.toDomain(): Playlist {
        return Playlist(
            id = id,
            name = name,
            description = description,
            coverPath = coverPath,
            trackIds = gson.fromJson(trackIdsJson, object : TypeToken<List<Long>>() {}.type),
            tracksCount = tracksCount
        )
    }

    private fun Playlist.toEntity(gson: Gson): PlaylistEntity {
        return PlaylistEntity(
            id = id,
            name = name,
            description = description,
            coverPath = coverPath,
            trackIdsJson = gson.toJson(trackIds),
            tracksCount = tracksCount
        )
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        try {
            // Сохраняем трек в базу (если его еще нет)
            tracksDao.insertTrack(track.toPlaylistTrackEntity())

            // Обновляем плейлист
            val updatedTrackIds = playlist.trackIds + track.trackId
            val updatedPlaylist = playlist.copy(
                trackIds = updatedTrackIds,
                tracksCount = updatedTrackIds.size
            )

            playlistDao.update(updatedPlaylist.toEntity(gson))
        } catch (e: Exception) {
            throw PlaylistRepositoryException("Failed to add track to playlist", e)
        }
    }
}

class PlaylistRepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause)