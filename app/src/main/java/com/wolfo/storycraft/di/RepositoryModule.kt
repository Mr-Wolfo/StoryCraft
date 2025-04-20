package com.wolfo.storycraft.di

import com.wolfo.storycraft.data.local.db.AppDb
import com.wolfo.storycraft.data.local.db.LocalDataSource
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.repository.StoryRepositoryImpl
import com.wolfo.storycraft.data.repository.UserRepositoryImpl
import com.wolfo.storycraft.domain.repository.StoryRepository
import com.wolfo.storycraft.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<RemoteDataSource> {
        RemoteDataSource(get())
    }

    single<LocalDataSource> {
        LocalDataSource(get<AppDb>().storyDao, get<AppDb>().userDao)
    }

    single<StoryRepository> {
        StoryRepositoryImpl(get(), get())
    }

    single<UserRepository> {
        UserRepositoryImpl(get(), get(), get())
    }
}
