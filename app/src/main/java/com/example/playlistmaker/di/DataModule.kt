package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val ITUNES_BASE_URL = "https://itunes.apple.com/"
private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

val dataModule = module {

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single {
        androidContext().getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    factory { MediaPlayer() }

    single<ExecutorService> { Executors.newCachedThreadPool() }
}