package com.practicum.playlistmaker1.settings.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker1.sharing.domain.SharingManager
import com.practicum.playlistmaker1.settings.domain.ThemeManager
import com.practicum.playlistmaker1.sharing.domain.Event

class SettingsViewModel(
    private val themeManager: ThemeManager,
    private val sharingManager: SharingManager
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> get() = _themeState

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> get() = _event

    init {
        _themeState.value = themeManager.isDarkThemeEnabled()
    }

    fun switchTheme(enabled: Boolean) {
        themeManager.setDarkThemeEnabled(enabled)
        _themeState.value = enabled
    }

    fun shareApp() {
        _event.value = sharingManager.shareApp() // Вызываем метод без передачи Context
    }

    fun openSupport() {
        _event.value = sharingManager.openSupport() // Вызываем метод без передачи Context
    }

    fun openPrivacyAgreement() {
        _event.value = sharingManager.openPrivacyAgreement() // Вызываем метод без передачи Context
    }
}
