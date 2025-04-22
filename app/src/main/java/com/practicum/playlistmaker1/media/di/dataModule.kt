package com.practicum.playlistmaker1.media.di

import android.content.Context
import androidx.room.Room
import com.practicum.playlistmaker1.media.data.FavouriteTracksRepositoryImpl
import com.practicum.playlistmaker1.media.data.db.AppDatabase
import com.practicum.playlistmaker1.media.domain.FavouriteTracksRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { provideDatabase(androidContext()) }
    single { provideFavouriteTracksDao(get()) }
    single<FavouriteTracksRepository> { FavouriteTracksRepositoryImpl(get(), get()) }
}

private fun provideDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "database.db"
    ).build()
}

private fun provideFavouriteTracksDao(database: AppDatabase) = database.favouriteTracksDao()