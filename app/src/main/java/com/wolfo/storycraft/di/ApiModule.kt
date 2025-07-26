package com.wolfo.storycraft.di

import com.wolfo.storycraft.data.remote.ApiService
import com.wolfo.storycraft.data.remote.AuthInterceptor
import com.wolfo.storycraft.data.remote.TokenAuthenticator
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://wolvion.ru" // МОЙ ДОМЕН

val apiModule = module {

    single<Retrofit.Builder> {
        Retrofit.Builder()
    }

    single<OkHttpClient> {

        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(get()))
            .authenticator(TokenAuthenticator(
                tokenManager = get(),
                retrofitBuilder = get(),
                baseUrl = BASE_URL
            )) // Добавляем аутентификатор
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
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