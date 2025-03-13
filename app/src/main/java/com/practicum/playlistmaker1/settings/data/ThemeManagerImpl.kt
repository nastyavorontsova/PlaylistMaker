package com.practicum.playlistmaker1.settings.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.practicum.playlistmaker1.settings.domain.ThemeManager

class ThemeManagerImpl(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) : ThemeManager {

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
        private const val FIRST_RUN_KEY = "first_run"
    }

    override fun isDarkThemeEnabled(): Boolean {
        if (isFirstRun()) {
            setDarkThemeEnabled(isSystemDarkThemeEnabled())
            setFirstRun(false)
        }
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(DARK_THEME_KEY, enabled)
            apply()
        }
    }

    private fun isSystemDarkThemeEnabled(): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun isFirstRun(): Boolean {
        return sharedPreferences.getBoolean(FIRST_RUN_KEY, true)
    }

    private fun setFirstRun(isFirstRun: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(FIRST_RUN_KEY, isFirstRun)
            apply()
        }
    }
}
