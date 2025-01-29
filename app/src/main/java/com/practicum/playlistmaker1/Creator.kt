package com.practicum.playlistmaker1

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker1.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker1.data.ThemeManagerImpl
import com.practicum.playlistmaker1.data.TrackRepositoryImpl
import com.practicum.playlistmaker1.data.network.RetrofitClient
import com.practicum.playlistmaker1.domain.ThemeManager
import com.practicum.playlistmaker1.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker1.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker1.domain.api.TrackInteractor
import com.practicum.playlistmaker1.domain.api.TrackRepository
import com.practicum.playlistmaker1.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker1.presentation.App


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
}