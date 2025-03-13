package com.practicum.playlistmaker1.search.di.data

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPreferencesModule = module {
    single {
        androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }
}
