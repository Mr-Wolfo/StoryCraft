package com.wolfo.storycraft.di

import com.wolfo.storycraft.data.remote.ApiService
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {

    val BASE_URL = "http://192.168.45.229:8000" // ЛОКАЛЬНЫЙ

    single<OkHttpClient> {
        OkHttpClient.Builder().build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ApiService> {
        get<Retrofit>().create(ApiService::class.java)
    }
}