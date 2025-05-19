package com.wolfo.storycraft.di

import com.google.gson.Gson
import com.wolfo.storycraft.data.local.data_store.AuthTokenManager
import com.wolfo.storycraft.data.local.data_store.AuthTokenManagerImpl
import com.wolfo.storycraft.data.utils.NetworkHandler
import com.wolfo.storycraft.data.utils.RepositoryHandler
import org.koin.dsl.module

val appModule = module {
    single<AuthTokenManager> { AuthTokenManagerImpl(get()) }

    factory<NetworkHandler> {
        NetworkHandler()
    }

    factory<RepositoryHandler> {
        RepositoryHandler()
    }

    single<Gson> {
        Gson()
    }
}