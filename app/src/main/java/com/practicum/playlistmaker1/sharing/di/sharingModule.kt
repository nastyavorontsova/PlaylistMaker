package com.practicum.playlistmaker1.sharing.di

import com.practicum.playlistmaker1.sharing.data.SharingManagerImpl
import com.practicum.playlistmaker1.sharing.domain.SharingManager
import org.koin.dsl.module

val sharingModule = module {
    single<SharingManager> { SharingManagerImpl() }
}