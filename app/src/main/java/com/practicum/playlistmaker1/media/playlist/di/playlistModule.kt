package com.practicum.playlistmaker1.media.playlist.di

import com.google.gson.Gson
import com.practicum.playlistmaker1.media.favorites.data.db.AppDatabase
import com.practicum.playlistmaker1.media.playlist.data.db.PlaylistRepositoryImpl
import com.practicum.playlistmaker1.media.playlist.domain.PlaylistRepository
import com.practicum.playlistmaker1.media.playlist.domain.PlaylistsInteractor
import com.practicum.playlistmaker1.media.playlist.ui.EditPlaylistViewModel
import com.practicum.playlistmaker1.media.playlist.ui.PlaylistViewModel
import com.practicum.playlistmaker1.media.playlist.ui.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            playlistDao = get(),
            tracksDao = get(),
            context = androidContext(),
            gson = get()
        )
    }

    single { Gson() }

    factory { PlaylistsInteractor(get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel {
        PlaylistViewModel(get(), get()) // get<Application>(), get<PlaylistRepository>()
    }

    viewModel {
        EditPlaylistViewModel(get(), get()) // get<Application>(), get<PlaylistRepository>()
    }
}