package com.practicum.playlistmaker1.search.di.data

import com.practicum.playlistmaker1.search.data.NetworkClient
import com.practicum.playlistmaker1.search.data.network.RetrofitClient
import org.koin.dsl.module

val networkModule = module {
    single<NetworkClient> { RetrofitClient() }
}