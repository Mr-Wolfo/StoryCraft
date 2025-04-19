package com.wolfo.storycraft.di

import com.wolfo.storycraft.data.local.db.AppDb
import com.wolfo.storycraft.data.local.db.LocalDataSource
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.repository.StoryRepositoryImpl
import com.wolfo.storycraft.domain.repository.StoryRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<RemoteDataSource> {
        RemoteDataSource(get())
    }

    single<LocalDataSource> {
        LocalDataSource(get<AppDb>().storyDao)
    }

    single<StoryRepository> {
        StoryRepositoryImpl(get(), get())
    }
}
