package com.practicum.playlistmaker1.app.di

import android.content.Context
import com.practicum.playlistmaker1.settings.data.ThemeManagerImpl
import com.practicum.playlistmaker1.settings.domain.ThemeManager
import org.koin.dsl.module

val appModule = module {

    // ThemeManager
    single<ThemeManager> { ThemeManagerImpl(get()) }
}