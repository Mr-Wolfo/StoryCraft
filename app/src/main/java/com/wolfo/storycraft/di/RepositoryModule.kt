package com.wolfo.storycraft.di

import com.wolfo.storycraft.data.local.db.LocalDataSource
import com.wolfo.storycraft.data.local.db.LocalDataSourceImpl
import com.wolfo.storycraft.data.local.db.StoryAppDatabase
import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.remote.RemoteDataSourceImpl
import com.wolfo.storycraft.data.repository.AuthRepositoryImpl
import com.wolfo.storycraft.data.repository.StoryRepositoryImpl
import com.wolfo.storycraft.data.repository.UserRepositoryImpl
import com.wolfo.storycraft.domain.repository.AuthRepository
import com.wolfo.storycraft.domain.repository.StoryRepository
import com.wolfo.storycraft.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<RemoteDataSource> {
        RemoteDataSourceImpl(get())
    }

    single<LocalDataSource> {
        LocalDataSourceImpl(get(),
            get<StoryAppDatabase>().userDao(),
            get<StoryAppDatabase>().tagDao(),
            get<StoryAppDatabase>().storyDao(),
            get<StoryAppDatabase>().pageDao(),
            get<StoryAppDatabase>().choiceDao(),
            get<StoryAppDatabase>().reviewDao(),
            get<StoryAppDatabase>().storyTagCrossRefDao())
    }

    single<StoryRepository> {
        StoryRepositoryImpl(get(), get(), get(), get())
    }

    single<UserRepository> {
        UserRepositoryImpl(get(), get(), get(), get(), get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(get(), get(), get(), get())
    }
}
