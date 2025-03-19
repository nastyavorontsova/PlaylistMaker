package com.practicum.playlistmaker1.sharing.domain

sealed class EventType {
    object ShareApp : EventType()
    object OpenSupport : EventType()
    object OpenPrivacyAgreement : EventType()
}