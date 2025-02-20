package com.practicum.playlistmaker1.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker1.creator.App
import com.practicum.playlistmaker1.settings.domain.ThemeManager
import com.practicum.playlistmaker1.sharing.domain.SharingManager

class SettingsViewModelFactory(
    private val themeManager: ThemeManager,
    private val sharingManager: SharingManager,
    private val app: App
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(themeManager, sharingManager, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}