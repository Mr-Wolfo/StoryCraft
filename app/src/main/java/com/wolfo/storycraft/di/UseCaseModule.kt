package com.wolfo.storycraft.di

import com.wolfo.storycraft.domain.usecase.LoadStoryFullByIdUseCase
import com.wolfo.storycraft.domain.usecase.ObserveStoriesUseCase
import com.wolfo.storycraft.domain.usecase.ObserveStoryBaseByIdUseCase
import com.wolfo.storycraft.domain.usecase.ObserveStoryFullByIdUseCase
import com.wolfo.storycraft.domain.usecase.RefreshStoriesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<ObserveStoriesUseCase> {
        ObserveStoriesUseCase(get())
    }
    factory<LoadStoryFullByIdUseCase> {
        LoadStoryFullByIdUseCase(get())
    }
    factory<RefreshStoriesUseCase> {
        RefreshStoriesUseCase(get())
    }
    factory<ObserveStoryBaseByIdUseCase> {
        ObserveStoryBaseByIdUseCase(get())
    }
    factory<ObserveStoryFullByIdUseCase> {
        ObserveStoryFullByIdUseCase(get())
    }
}