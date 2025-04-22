package com.practicum.playlistmaker1.media.di

import com.practicum.playlistmaker1.media.data.FavouriteTracksRepositoryImpl
import com.practicum.playlistmaker1.media.data.db.TrackDbConvertor
import com.practicum.playlistmaker1.media.domain.FavouriteTracksRepository
import org.koin.dsl.module

val repositoryMediaModule = module {
    single<FavouriteTracksRepository> { FavouriteTracksRepositoryImpl(get(), get()) }
    factory { TrackDbConvertor() }
}