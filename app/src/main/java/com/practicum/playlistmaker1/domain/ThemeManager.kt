package com.practicum.playlistmaker1.domain

interface ThemeManager {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}