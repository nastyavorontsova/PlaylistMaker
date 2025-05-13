package com.practicum.playlistmaker1.media.favorites.di

import com.practicum.playlistmaker1.media.favorites.domain.FavouriteTracksInteractor
import org.koin.dsl.module

val interactorMediaModule = module {
    factory { FavouriteTracksInteractor(get()) }
}