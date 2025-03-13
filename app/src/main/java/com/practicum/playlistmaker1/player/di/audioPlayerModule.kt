package com.practicum.playlistmaker1.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker1.player.ui.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val audioPlayerModule = module {
    single { MediaPlayer() } // MediaPlayer как зависимость
    viewModel { AudioPlayerViewModel(get()) } // ViewModel
}