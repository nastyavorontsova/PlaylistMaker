package com.practicum.playlistmaker1.settings.domain

interface ThemeManager {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}