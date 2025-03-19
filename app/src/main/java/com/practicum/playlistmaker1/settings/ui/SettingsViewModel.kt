package com.practicum.playlistmaker1.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker1.sharing.domain.SharingManager
import com.practicum.playlistmaker1.settings.domain.ThemeManager
import com.practicum.playlistmaker1.app.Event
import com.practicum.playlistmaker1.sharing.domain.EventType

class SettingsViewModel(
    private val themeManager: ThemeManager,
    private val sharingManager: SharingManager
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> get() = _themeState

    private val _event = MutableLiveData<Event<EventType>>()
    val event: LiveData<Event<EventType>> get() = _event

    init {
        _themeState.value = themeManager.isDarkThemeEnabled()
    }

    fun switchTheme(enabled: Boolean) {
        themeManager.setDarkThemeEnabled(enabled)
        _themeState.value = enabled
    }

    fun shareApp() {
        _event.value = Event(EventType.ShareApp)
    }

    fun openSupport() {
        _event.value = Event(EventType.OpenSupport)
    }

    fun openPrivacyAgreement() {
        _event.value = Event(EventType.OpenPrivacyAgreement)
    }

}
