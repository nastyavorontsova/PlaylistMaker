package com.practicum.playlistmaker1.creator

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker1.main.ui.MainActivity
import com.practicum.playlistmaker1.settings.domain.ThemeManager
import com.practicum.playlistmaker1.settings.ui.SettingsActivity

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    private lateinit var themeManager: ThemeManager
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        instance = this

        themeManager = Creator.provideThemeManager(this)
        darkTheme = themeManager.isDarkThemeEnabled()

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        themeManager.setDarkThemeEnabled(darkThemeEnabled)
    }
}
