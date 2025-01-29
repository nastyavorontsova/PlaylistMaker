package com.practicum.playlistmaker1.data

import android.content.SharedPreferences
import com.practicum.playlistmaker1.domain.ThemeManager

class ThemeManagerImpl(private val sharedPreferences: SharedPreferences) : ThemeManager {

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(DARK_THEME_KEY, enabled)
            apply()
        }
    }
}
