package com.practicum.playlistmaker1.media.playlist.di

import com.practicum.playlistmaker1.media.playlist.ui.CreatedPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val createdPlaylistModule = module {
    viewModel { params ->
        CreatedPlaylistViewModel(
            interactor = get()
        )
    }
}