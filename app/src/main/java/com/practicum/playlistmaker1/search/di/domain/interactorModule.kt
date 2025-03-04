package com.practicum.playlistmaker1.search.di.domain

import com.practicum.playlistmaker1.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker1.search.domain.api.TrackInteractor
import com.practicum.playlistmaker1.search.domain.impl.TrackInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<SearchHistoryInteractor> { SearchHistoryInteractor(get()) }
    single<TrackInteractor> { TrackInteractorImpl(get()) }
    }
