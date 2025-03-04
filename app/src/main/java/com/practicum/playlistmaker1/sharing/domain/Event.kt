package com.practicum.playlistmaker1.sharing.domain

sealed class Event {
    object ShareApp : Event()
    object OpenSupport : Event()
    object OpenPrivacyAgreement : Event()
}