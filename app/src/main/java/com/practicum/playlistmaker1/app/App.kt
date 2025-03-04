package com.practicum.playlistmaker1.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.DrawableCompat.applyTheme
import com.practicum.playlistmaker1.app.di.appModule
import com.practicum.playlistmaker1.player.di.audioPlayerModule
import com.practicum.playlistmaker1.search.di.data.networkModule
import com.practicum.playlistmaker1.search.di.data.repositoryModule
import com.practicum.playlistmaker1.search.di.data.sharedPreferencesModule
import com.practicum.playlistmaker1.search.di.domain.interactorModule
import com.practicum.playlistmaker1.search.di.ui.searchViewModelModule
import com.practicum.playlistmaker1.settings.di.settingsViewModelModule
import com.practicum.playlistmaker1.settings.domain.ThemeManager
import com.practicum.playlistmaker1.sharing.di.sharingModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Инициализация Koin
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    appModule,
                    audioPlayerModule,
                    networkModule,
                    repositoryModule,
                    sharedPreferencesModule,
                    interactorModule,
                    sharingModule,
                    settingsViewModelModule,
                    searchViewModelModule
                )
            )
        }

        // Применение темы после инициализации Koin
        val themeManager: ThemeManager by inject()
        applyTheme(themeManager.isDarkThemeEnabled())
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}