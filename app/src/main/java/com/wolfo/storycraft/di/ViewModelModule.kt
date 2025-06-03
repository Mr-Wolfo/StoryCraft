package com.wolfo.storycraft.di

import com.wolfo.storycraft.presentation.common.AuthStateViewModel
import com.wolfo.storycraft.presentation.features.profile.ProfileViewModel
import com.wolfo.storycraft.presentation.features.profile.auth.AuthFormViewModel
import com.wolfo.storycraft.presentation.features.profile.auth.AuthViewModel
import com.wolfo.storycraft.presentation.features.story_editor.StoryEditorViewModel
import com.wolfo.storycraft.presentation.features.story_list.SearchAndFilterViewModel
import com.wolfo.storycraft.presentation.features.story_list.StoryListViewModel
import com.wolfo.storycraft.presentation.features.story_view.details.StoryDetailsViewModel
import com.wolfo.storycraft.presentation.features.story_view.reader.StoryReaderViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

//    viewModelOf(::StoryListViewModel)
    viewModel<StoryListViewModel> {
        StoryListViewModel(get(), get())
    }

    viewModel<SearchAndFilterViewModel> {
        SearchAndFilterViewModel()
    }

    viewModel<StoryReaderViewModel> {
        StoryReaderViewModel(get(), get())
    }
    viewModel<StoryDetailsViewModel> {
        StoryDetailsViewModel(get(), get(), get(), get(), get(), get(), get())
    }

    viewModel<StoryEditorViewModel> {
        StoryEditorViewModel(get(), get(), get(), get(), get(), get(), get(), get())
    }

    viewModel<AuthViewModel> {
        AuthViewModel(get(), get())
    }

    viewModel<AuthStateViewModel> {
        AuthStateViewModel(get(), get())
    }

    viewModel<AuthFormViewModel> {
        AuthFormViewModel()
    }
    viewModel<ProfileViewModel> {
        ProfileViewModel(get(), get(), get(), get(), get())
    }
}