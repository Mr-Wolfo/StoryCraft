package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.StoryFull
import com.wolfo.storycraft.domain.repository.StoryRepository
import java.io.File

/**
 * Use Case для создания новой истории.
 */
class CreateStoryUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        tags: List<String>,
        coverImageFile: File?
    ): ResultM<StoryFull> {
        return storyRepository.createStory(title, description, tags, coverImageFile)
    }
}