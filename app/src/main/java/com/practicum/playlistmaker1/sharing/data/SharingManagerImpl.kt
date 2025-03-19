package com.practicum.playlistmaker1.sharing.data

import com.practicum.playlistmaker1.sharing.domain.SharingManager
import com.practicum.playlistmaker1.sharing.domain.EventType

class SharingManagerImpl : SharingManager {

    override fun shareApp(): EventType {
        return EventType.ShareApp
    }

    override fun openSupport(): EventType {
        return EventType.OpenSupport
    }

    override fun openPrivacyAgreement(): EventType {
        return EventType.OpenPrivacyAgreement
    }
}