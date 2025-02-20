package com.practicum.playlistmaker1.creator

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker1.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker1.settings.data.ThemeManagerImpl
import com.practicum.playlistmaker1.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker1.search.data.network.RetrofitClient
import com.practicum.playlistmaker1.settings.domain.ThemeManager
import com.practicum.playlistmaker1.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker1.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker1.search.domain.api.TrackInteractor
import com.practicum.playlistmaker1.search.domain.api.TrackRepository
import com.practicum.playlistmaker1.search.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker1.sharing.data.SharingManagerImpl
import com.practicum.playlistmaker1.sharing.domain.SharingManager


object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideThemeManager(context: Context): ThemeManager {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return ThemeManagerImpl(sharedPreferences)
    }

    private fun getSharedPreferences(): SharedPreferences {
        return App.instance.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    private fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getSharedPreferences())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractor(provideSearchHistoryRepository())
    }

    fun provideSharingManager(context: Context): SharingManager {
        return SharingManagerImpl(context)
    }
}