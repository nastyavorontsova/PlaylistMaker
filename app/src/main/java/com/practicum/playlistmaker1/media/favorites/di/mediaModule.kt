package com.practicum.playlistmaker1.media.favorites.di

import com.practicum.playlistmaker1.media.favorites.ui.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { FavoritesViewModel(get()) }
}