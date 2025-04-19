package com.wolfo.storycraft.data.mapper

import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import com.wolfo.storycraft.data.remote.dto.ChoiceDto
import com.wolfo.storycraft.data.remote.dto.PageDto
import com.wolfo.storycraft.data.remote.dto.StoryBaseDto
import com.wolfo.storycraft.data.remote.dto.StoryDto
import com.wolfo.storycraft.domain.model.Choice
import com.wolfo.storycraft.domain.model.Page
import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.model.StoryBase

fun StoryBaseDto.toDomain(): StoryBase {
    return StoryBase(
        id = this.id,
        title = this.title,
        description = this.description ?: "Empty",
        authorId = this.authorId,
        tags = this.tags,
        averageRating = null
    )
}

fun StoryDto.toDomain(): Story {
    return Story(
        id = this.id,
        authorId = this.authorId,
        startPageId = this.startPageId,
        description = this.description,
        title = this.title,
        tags = this.tags,
        coverImageUrl = this.coverImageUrl,
        isPublished = this.isPublished,
        pages = this.pages.map { it.toDomain() }
    )
}

fun PageDto.toDomain(): Page {
    return Page(
        id = this.id,
        storyId = this.storyId,
        pageText = this.pageText,
        isEndingPage = this.isEndingPage,
        choices = this.choices.map { it.toDomain() }
    )
}

fun ChoiceDto.toDomain(): Choice {
    return Choice(
        id = this.id,
        pageId = this.pageId,
        choiceText = this.choiceText,
        targetPageId = this.targetPageId
    )
}

fun StoryEntity.toDomain(): StoryBase {
    return StoryBase(
        id = this.id,
        title = this.title,
        description = this.description ?: "Empty",
        authorId = this.authorId ?: -1L,
        tags = null,
        averageRating = this.averageRating
    )
}

fun StoryBaseDto.toEntity(): StoryEntity {
    return StoryEntity(
        id = this.id,
        title = this.title,
        description = this.description ?: "Empty",
        authorId = this.authorId,
        authorName = "Test",
//        tags = this.tags,
        averageRating = this.averageRating,
        startPageId = this.startPageId,
        coverImageUrl = this.coverImageUrl,
        isPublished = this.isPublished
    )
}