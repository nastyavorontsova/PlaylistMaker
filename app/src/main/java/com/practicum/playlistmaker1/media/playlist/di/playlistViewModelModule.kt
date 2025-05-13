package com.practicum.playlistmaker1.media.playlist.di

import com.practicum.playlistmaker1.media.playlist.ui.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistViewModelModule = module {
    viewModel { PlaylistViewModel(get()) }
}