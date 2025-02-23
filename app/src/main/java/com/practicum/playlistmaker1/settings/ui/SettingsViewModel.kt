package com.practicum.playlistmaker1.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker1.sharing.domain.SharingManager
import com.practicum.playlistmaker1.settings.domain.ThemeManager

class SettingsViewModel(
    private val themeManager: ThemeManager,
    private val sharingManager: SharingManager
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> get() = _themeState

    init {
        // Инициализация начального состояния темы
        _themeState.value = themeManager.isDarkThemeEnabled()
    }

    fun switchTheme(enabled: Boolean) {
        themeManager.setDarkThemeEnabled(enabled)
        _themeState.value = enabled // Обновляем состояние темы
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
