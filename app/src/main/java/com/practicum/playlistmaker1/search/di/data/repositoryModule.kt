package com.practicum.playlistmaker1.search.di.data

import com.practicum.playlistmaker1.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker1.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker1.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker1.search.domain.api.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }
}