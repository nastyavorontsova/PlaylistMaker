package com.practicum.playlistmaker1

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean("dark_theme", false)

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

        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("dark_theme", darkThemeEnabled)
            apply()
        }
    }
}