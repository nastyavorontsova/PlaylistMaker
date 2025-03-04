package com.practicum.playlistmaker1.settings.di

import com.practicum.playlistmaker1.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule = module {
    viewModel { SettingsViewModel(get(), get()) }
}