package com.wolfo.storycraft.di

import com.wolfo.storycraft.domain.usecase.auth.CheckLoginStatusUseCase
import com.wolfo.storycraft.domain.usecase.auth.GetAccessTokenUseCase
import com.wolfo.storycraft.domain.usecase.auth.LoginUserUseCase
import com.wolfo.storycraft.domain.usecase.auth.LogoutUserUseCase
import com.wolfo.storycraft.domain.usecase.auth.RegisterUserUseCase
import com.wolfo.storycraft.domain.usecase.story.CreateReviewUseCase
import com.wolfo.storycraft.domain.usecase.story.DeleteReviewUseCase
import com.wolfo.storycraft.domain.usecase.story.DeleteStoryDraftUseCase
import com.wolfo.storycraft.domain.usecase.story.DeleteStoryUseCase
import com.wolfo.storycraft.domain.usecase.story.GetDraftStoriesUseCase
import com.wolfo.storycraft.domain.usecase.story.GetReviewsStreamUseCase
import com.wolfo.storycraft.domain.usecase.story.GetStoriesStreamUseCase
import com.wolfo.storycraft.domain.usecase.story.GetStoryBaseUseCase
import com.wolfo.storycraft.domain.usecase.story.GetStoryDetailsStreamUseCase
import com.wolfo.storycraft.domain.usecase.story.GetStoryDraftUseCase
import com.wolfo.storycraft.domain.usecase.story.PublishStoryUseCase
import com.wolfo.storycraft.domain.usecase.story.SaveStoryDraftUseCase
import com.wolfo.storycraft.domain.usecase.story.UpdateReviewUseCase
import com.wolfo.storycraft.domain.usecase.story.UpdateStoryUseCase
import com.wolfo.storycraft.domain.usecase.user.GetCurrentUserIdUseCase
import com.wolfo.storycraft.domain.usecase.user.GetCurrentUserStreamUseCase
import com.wolfo.storycraft.domain.usecase.user.GetUserProfileUseCase
import com.wolfo.storycraft.domain.usecase.user.RefreshCurrentUserUseCase
import com.wolfo.storycraft.domain.usecase.user.UpdateCurrentUserAvatarUseCase
import com.wolfo.storycraft.domain.usecase.user.UpdateCurrentUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<CheckLoginStatusUseCase> {
        CheckLoginStatusUseCase(get())
    }
    factory<GetAccessTokenUseCase> {
        GetAccessTokenUseCase(get())
    }
    factory<LoginUserUseCase> {
        LoginUserUseCase(get())
    }
    factory<LogoutUserUseCase> {
        LogoutUserUseCase(get())
    }
    factory<RegisterUserUseCase> {
        RegisterUserUseCase(get())
    }
    factory<CreateReviewUseCase> {
        CreateReviewUseCase(get())
    }

    factory<DeleteReviewUseCase> {
        DeleteReviewUseCase(get())
    }
    factory<DeleteStoryUseCase> {
        DeleteStoryUseCase(get())
    }
    factory<GetReviewsStreamUseCase> {
        GetReviewsStreamUseCase(get())
    }
    factory<GetStoriesStreamUseCase> {
        GetStoriesStreamUseCase(get())
    }
    factory<GetStoryBaseUseCase> {
        GetStoryBaseUseCase(get())
    }
    factory<GetStoryDetailsStreamUseCase> {
        GetStoryDetailsStreamUseCase(get())
    }
    factory<UpdateReviewUseCase> {
        UpdateReviewUseCase(get())
    }
    factory<DeleteStoryDraftUseCase> {
        DeleteStoryDraftUseCase(get())
    }
    factory<GetDraftStoriesUseCase> {
        GetDraftStoriesUseCase(get(), get())
    }
    factory<GetStoryDraftUseCase> {
        GetStoryDraftUseCase(get())
    }
    factory<PublishStoryUseCase> {
        PublishStoryUseCase(get(), get())
    }
    factory<SaveStoryDraftUseCase> {
        SaveStoryDraftUseCase(get())
    }
    factory<UpdateStoryUseCase> {
        UpdateStoryUseCase(get())
    }
    factory<GetCurrentUserStreamUseCase> {
        GetCurrentUserStreamUseCase(get())
    }
    factory<GetUserProfileUseCase> {
        GetUserProfileUseCase(get())
    }
    factory<RefreshCurrentUserUseCase> {
        RefreshCurrentUserUseCase(get())
    }
    factory<UpdateCurrentUserUseCase> {
        UpdateCurrentUserUseCase(get())
    }
    factory<UpdateCurrentUserAvatarUseCase> {
        UpdateCurrentUserAvatarUseCase(get())
    }
    factory<GetCurrentUserIdUseCase> {
        GetCurrentUserIdUseCase(get())
    }
}