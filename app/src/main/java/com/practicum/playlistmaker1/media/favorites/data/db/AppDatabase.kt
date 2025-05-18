package com.practicum.playlistmaker1.media.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker1.media.favorites.data.db.dao.FavouriteTracksDao
import com.practicum.playlistmaker1.media.favorites.data.db.entity.FavouriteTrackEntity
import com.practicum.playlistmaker1.media.playlist.data.db.dao.PlaylistDao
import com.practicum.playlistmaker1.media.playlist.data.db.dao.PlaylistTracksDao
import com.practicum.playlistmaker1.media.playlist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker1.media.playlist.data.db.entity.PlaylistTrackEntity

@Database(
    entities = [
        FavouriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteTracksDao(): FavouriteTracksDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTracksDao(): PlaylistTracksDao
}