package com.wolfo.storycraft.di

import com.google.gson.Gson
import com.wolfo.storycraft.data.local.data_store.AuthTokenManager
import com.wolfo.storycraft.data.local.data_store.AuthTokenManagerImpl
import com.wolfo.storycraft.data.mapper.DraftContentToStoryDraftEntityMapper
import com.wolfo.storycraft.data.mapper.PublishContentToStoryCreateDtoMapper
import com.wolfo.storycraft.data.mapper.StoryDraftEntityToDraftContentMapper
import com.wolfo.storycraft.data.utils.NetworkHandler
import com.wolfo.storycraft.data.utils.RepositoryHandler
import com.wolfo.storycraft.presentation.features.story_list.NativeAdManager
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import org.koin.dsl.module

val appModule = module {
    single<AuthTokenManager> {
        AuthTokenManagerImpl(get())
    }

    factory<NetworkHandler> {
        NetworkHandler()
    }

    single<NativeAdManager> {
        NativeAdManager(get(), "demo-native-content-yandex")
    }

    factory<RepositoryHandler> {
        RepositoryHandler()
    }

    factory<StoryDraftEntityToDraftContentMapper> {
        StoryDraftEntityToDraftContentMapper(get())
    }

    factory<PublishContentToStoryCreateDtoMapper> {
        PublishContentToStoryCreateDtoMapper()
    }

    factory<DraftContentToStoryDraftEntityMapper> {
        DraftContentToStoryDraftEntityMapper(get())
    }

    single<Gson> {
        Gson()
    }
}