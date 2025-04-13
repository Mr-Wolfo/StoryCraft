package com.wolfo.storycraft.di

import com.wolfo.storycraft.domain.usecase.GetStoriesUseCase
import com.wolfo.storycraft.domain.usecase.GetStoryFullUseCase
import com.wolfo.storycraft.presentation.features.storyeditor.GetStoryGraphDataUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<GetStoriesUseCase> {
        GetStoriesUseCase(get())
    }
    factory<GetStoryFullUseCase> {
        GetStoryFullUseCase(get())
    }
}