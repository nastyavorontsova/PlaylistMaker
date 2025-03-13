package com.practicum.playlistmaker1.search.di.data

import com.google.gson.Gson
import com.practicum.playlistmaker1.search.data.NetworkClient
import com.practicum.playlistmaker1.search.data.network.ApiService
import com.practicum.playlistmaker1.search.data.network.RetrofitNetworkClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    single<ApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    factory { Gson() }

    single<NetworkClient> { RetrofitNetworkClient(get()) }
}