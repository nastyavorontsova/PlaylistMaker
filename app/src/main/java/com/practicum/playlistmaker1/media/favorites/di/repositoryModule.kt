package com.practicum.playlistmaker1.media.favorites.di

import com.practicum.playlistmaker1.media.favorites.data.FavouriteTracksRepositoryImpl
import com.practicum.playlistmaker1.media.favorites.data.db.TrackDbConvertor
import com.practicum.playlistmaker1.media.favorites.domain.FavouriteTracksRepository
import org.koin.dsl.module

val repositoryMediaModule = module {
    single<FavouriteTracksRepository> { FavouriteTracksRepositoryImpl(get(), get()) }
    factory { TrackDbConvertor() }
}