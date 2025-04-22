package com.practicum.playlistmaker1.media.di

import com.practicum.playlistmaker1.media.domain.FavouriteTracksInteractor
import org.koin.dsl.module

val interactorMediaModule = module {
    factory { FavouriteTracksInteractor(get()) }
}