package com.practicum.playlistmaker1.media.di

import com.practicum.playlistmaker1.media.ui.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { FavoritesViewModel(get()) }
}