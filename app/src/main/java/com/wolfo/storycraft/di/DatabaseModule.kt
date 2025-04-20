package com.wolfo.storycraft.di

import com.wolfo.storycraft.data.local.data_store.AuthTokenManager
import com.wolfo.storycraft.data.local.db.AppDb
import org.koin.dsl.module

val dataBaseModule = module {

    single<AppDb> {
        AppDb.createDataBase(get())
    }

    single<AuthTokenManager> {
        AuthTokenManager(get())
    }
}