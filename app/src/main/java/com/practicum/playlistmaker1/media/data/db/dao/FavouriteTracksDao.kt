package com.practicum.playlistmaker1.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker1.media.data.db.entity.FavouriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteTracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavouriteTrackEntity): Long

    @Delete
    suspend fun deleteTrack(track: FavouriteTrackEntity): Int

    @Query("SELECT * FROM favourite_tracks ORDER BY addedAt DESC")
    fun getAllFavouriteTracks(): Flow<List<FavouriteTrackEntity>>

    @Query("SELECT trackId FROM favourite_tracks")
    fun getAllFavouriteTrackIds(): Flow<List<Long>>
}