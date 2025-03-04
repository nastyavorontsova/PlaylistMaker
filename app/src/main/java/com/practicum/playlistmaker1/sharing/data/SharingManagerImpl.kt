package com.practicum.playlistmaker1.sharing.data

import com.practicum.playlistmaker1.sharing.domain.SharingManager
import com.practicum.playlistmaker1.sharing.domain.Event

class SharingManagerImpl : SharingManager {

    override fun shareApp(): Event {
        return Event.ShareApp
    }

    override fun openSupport(): Event {
        return Event.OpenSupport
    }

    override fun openPrivacyAgreement(): Event {
        return Event.OpenPrivacyAgreement
    }
}