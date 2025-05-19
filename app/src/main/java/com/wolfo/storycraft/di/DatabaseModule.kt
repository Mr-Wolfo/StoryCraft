package com.wolfo.storycraft.di

import com.wolfo.storycraft.data.local.data_store.AuthTokenManager
import com.wolfo.storycraft.data.local.data_store.AuthTokenManagerImpl
import com.wolfo.storycraft.data.local.db.StoryAppDatabase
import org.koin.dsl.module

val dataBaseModule = module {

    single<StoryAppDatabase> {
        StoryAppDatabase.getInstance(get())
    }

    single<AuthTokenManager> {
        AuthTokenManagerImpl(get())
    }
}