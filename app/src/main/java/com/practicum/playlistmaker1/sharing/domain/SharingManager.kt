package com.practicum.playlistmaker1.sharing.domain

interface SharingManager {
    fun shareApp(): EventType
    fun openSupport(): EventType
    fun openPrivacyAgreement(): EventType
}