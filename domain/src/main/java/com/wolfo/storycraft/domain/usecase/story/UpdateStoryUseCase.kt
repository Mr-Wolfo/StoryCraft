package com.wolfo.storycraft.domain.usecase.story

import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.story.StoryFull
import com.wolfo.storycraft.domain.repository.StoryRepository
import java.io.File

/**
 * Use Case для обновления истории.
 */
class UpdateStoryUseCase(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(
        storyId: String,
        title: String?,
        description: String?,
        tags: List<String>?,
        coverImageFile: File?
    ): ResultM<StoryFull> {
        return storyRepository.updateStory(storyId, title, description, tags, coverImageFile)
    }
}