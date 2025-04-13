package com.wolfo.storycraft.di

import com.wolfo.storycraft.data.remote.RemoteDataSource
import com.wolfo.storycraft.data.repository.StoryRepositoryImpl
import com.wolfo.storycraft.domain.repository.StoryRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<RemoteDataSource> {
        RemoteDataSource(get())
    }

    single<StoryRepository> {
        StoryRepositoryImpl(get())
    }
}
