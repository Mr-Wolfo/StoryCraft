package com.wolfo.storycraft.di

import com.wolfo.storycraft.presentation.features.storyeditor.StoryGraphViewModel
import com.wolfo.storycraft.presentation.features.storylist.StoryListViewModel
import com.wolfo.storycraft.presentation.features.storyreader.StoryReaderViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {

//    viewModelOf(::StoryListViewModel)
    viewModel<StoryListViewModel> {
        StoryListViewModel(getStoriesUseCase = get())
    }

    viewModel<StoryReaderViewModel> {
        StoryReaderViewModel(get(), get())
    }

}