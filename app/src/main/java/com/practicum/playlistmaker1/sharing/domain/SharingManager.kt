package com.practicum.playlistmaker1.sharing.domain

interface SharingManager {
    fun shareApp(): Event
    fun openSupport(): Event
    fun openPrivacyAgreement(): Event
}