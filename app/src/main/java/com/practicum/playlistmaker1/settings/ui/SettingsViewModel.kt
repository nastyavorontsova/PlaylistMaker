package com.practicum.playlistmaker1.settings.ui

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker1.creator.App
import com.practicum.playlistmaker1.settings.domain.ThemeManager
import com.practicum.playlistmaker1.sharing.domain.SharingManager

class SettingsViewModel(
    private val themeManager: ThemeManager,
    private val sharingManager: SharingManager,
    private val app: App
) : ViewModel() {

    fun isDarkThemeEnabled(): Boolean {
        return themeManager.isDarkThemeEnabled()
    }

    fun switchTheme(enabled: Boolean) {
        themeManager.setDarkThemeEnabled(enabled)
        app.switchTheme(enabled)
    }

    fun shareApp() {
        sharingManager.shareApp()
    }

    fun openSupport() {
        sharingManager.openSupport()
    }

    fun openPrivacyAgreement() {
        sharingManager.openPrivacyAgreement()
    }
}