package com.practicum.playlistmaker1.search.di.domain

import com.practicum.playlistmaker1.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker1.search.domain.api.SuspendTrackInteractor
import com.practicum.playlistmaker1.search.domain.impl.SuspendTrackInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<SearchHistoryInteractor> { SearchHistoryInteractor(get()) }
    single<SuspendTrackInteractor> { SuspendTrackInteractorImpl(get()) }
    }
