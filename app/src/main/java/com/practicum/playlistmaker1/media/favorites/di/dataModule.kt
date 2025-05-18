package com.practicum.playlistmaker1.media.favorites.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.practicum.playlistmaker1.media.favorites.data.FavouriteTracksRepositoryImpl
import com.practicum.playlistmaker1.media.favorites.data.db.AppDatabase
import com.practicum.playlistmaker1.media.favorites.domain.FavouriteTracksRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { provideDatabase(androidContext()) }
    single { provideFavouriteTracksDao(get()) }
    single { providePlaylistDao(get()) }
    single { providePlaylistTracksDao(get()) }
    single<FavouriteTracksRepository> { FavouriteTracksRepositoryImpl(get(), get()) }
}

private fun provideDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "app_database"
    )
        .addMigrations(MIGRATION_2_3)
        .build()
}

private fun provideFavouriteTracksDao(database: AppDatabase) = database.favouriteTracksDao()

private fun providePlaylistDao(database: AppDatabase) = database.playlistDao()

private fun providePlaylistTracksDao(database: AppDatabase) = database.playlistTracksDao()

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS playlist_tracks (
                trackId INTEGER PRIMARY KEY NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                collectionName TEXT,
                releaseDate TEXT NOT NULL,
                primaryGenreName TEXT NOT NULL,
                country TEXT NOT NULL,
                trackTimeMillis INTEGER NOT NULL,
                previewUrl TEXT NOT NULL,
                artworkUrl100 TEXT NOT NULL
            )
        """)
    }
}
