package com.practicum.playlistmaker1.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker1.media.data.db.dao.FavouriteTracksDao
import com.practicum.playlistmaker1.media.data.db.entity.FavouriteTrackEntity

@Database(
    entities = [FavouriteTrackEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteTracksDao(): FavouriteTracksDao
}