package com.practicum.playlistmaker1.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker1.app.di.appModule
import com.practicum.playlistmaker1.media.favorites.di.dataModule
import com.practicum.playlistmaker1.media.favorites.di.interactorMediaModule
import com.practicum.playlistmaker1.media.favorites.di.repositoryMediaModule
import com.practicum.playlistmaker1.media.favorites.di.mediaModule
import com.practicum.playlistmaker1.media.playlist.di.createdPlaylistModule
import com.practicum.playlistmaker1.media.playlist.di.playlistModule
import com.practicum.playlistmaker1.media.playlist.di.playlistViewModelModule
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
                    searchViewModelModule,
                    mediaModule,
                    dataModule,
                    repositoryMediaModule,
                    interactorMediaModule,
                    playlistModule,
                    playlistViewModelModule,
                    createdPlaylistModule
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