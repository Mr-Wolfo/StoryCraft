package com.wolfo.storycraft.di

import com.wolfo.storycraft.domain.usecase.LoadStoryFullByIdUseCase
import com.wolfo.storycraft.domain.usecase.LoginUseCase
import com.wolfo.storycraft.domain.usecase.LogoutUseCase
import com.wolfo.storycraft.domain.usecase.ObserveAuthTokenUseCase
import com.wolfo.storycraft.domain.usecase.ObserveStoriesUseCase
import com.wolfo.storycraft.domain.usecase.ObserveStoryBaseByIdUseCase
import com.wolfo.storycraft.domain.usecase.ObserveStoryFullByIdUseCase
import com.wolfo.storycraft.domain.usecase.ObserveUserProfileUseCase
import com.wolfo.storycraft.domain.usecase.RefreshStoriesUseCase
import com.wolfo.storycraft.domain.usecase.RegisterUseCase
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
    factory<RegisterUseCase> {
        RegisterUseCase(get())
    }
    factory<LoginUseCase> {
        LoginUseCase(get())
    }
    factory<LogoutUseCase> {
        LogoutUseCase(get())
    }
    factory<ObserveAuthTokenUseCase> {
        ObserveAuthTokenUseCase(get())
    }
    factory<ObserveUserProfileUseCase> {
        ObserveUserProfileUseCase(get())
    }
}