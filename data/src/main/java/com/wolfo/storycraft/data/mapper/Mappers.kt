package com.wolfo.storycraft.data.mapper

import com.wolfo.storycraft.data.local.db.dao.PageWithChoices
import com.wolfo.storycraft.data.local.db.dao.StoryWithPagesAndChoices
import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity
import com.wolfo.storycraft.data.local.db.entity.PageEntity
import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import com.wolfo.storycraft.data.local.db.entity.UserEntity
import com.wolfo.storycraft.data.remote.dto.AuthRequestDto
import com.wolfo.storycraft.data.remote.dto.ChoiceDto
import com.wolfo.storycraft.data.remote.dto.PageDto
import com.wolfo.storycraft.data.remote.dto.RegisterRequestDto
import com.wolfo.storycraft.data.remote.dto.StoryBaseDto
import com.wolfo.storycraft.data.remote.dto.StoryDto
import com.wolfo.storycraft.data.remote.dto.UserDto
import com.wolfo.storycraft.domain.model.AuthRequest
import com.wolfo.storycraft.domain.model.Choice
import com.wolfo.storycraft.domain.model.Page
import com.wolfo.storycraft.domain.model.RegisterRequest
import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.domain.model.User

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

fun StoryWithPagesAndChoices.toDomain(): Story {
    return Story(
        id = this.story.id,
        authorId = this.story.authorId ?: -1L,
        startPageId = this.story.startPageId ?: 0,
        description = this.story.description,
        title = this.story.title,
        tags = this.story.tags,
        coverImageUrl = this.story.coverImageUrl,
        isPublished = this.story.isPublished,
        pages = this.pages.map { it.toDomain() }
    )
}

fun PageWithChoices.toDomain(): Page {
    return Page(
        id = this.page.id,
        storyId = this.page.storyId,
        pageText = this.page.pageText,
        coverImageUrl = this.page.imageUrl,
        isEndingPage = this.page.isEndingPage,
        choices = this.choices.map{ it.toDomain() }
    )
}

fun ChoiceEntity.toDomain(): Choice {
    return Choice(
        id = this.id,
        pageId = this.pageId,
        choiceText = this.choiceText,
        targetPageId = this.targetPageId ?: -1,
    )
}

fun PageDto.toDomain(): Page {
    return Page(
        id = this.id,
        storyId = this.storyId,
        pageText = this.pageText,
        coverImageUrl = this.imageUrl,
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
        tags = this.tags,
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
        tags = this.tags,
        averageRating = this.averageRating,
        startPageId = this.startPageId,
        coverImageUrl = this.coverImageUrl,
        isPublished = this.isPublished
    )
}

fun RegisterRequest.toDto(): RegisterRequestDto {
    return RegisterRequestDto(
        userName = this.userName,
        email = this.email,
        password = this.password
    )
}

fun AuthRequest.toDto(): AuthRequestDto {
    return AuthRequestDto(
        name = this.name,
        password = this.password
    )
}

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        userName = this.userName,
        email = this.email
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        userName = this.userName,
        email = this.email
    )
}

fun StoryDto.toEntity(): StoryEntity = StoryEntity(
    id = this.id ?: -1L, // Дефолтное значение если null
    title = this.title ?: "",
    description = this.description,
    authorId = this.authorId,
    authorName = "Unknown",
    coverImageUrl = this.coverImageUrl,
    startPageId = this.startPageId,
    tags = this.tags?.mapNotNull { it } ?: emptyList(), // Защита от null в списке
    isPublished = this.isPublished ?: false,
    averageRating = this.averageRating,
    lastRefreshed = System.currentTimeMillis()
)

// PageDto -> PageEntity
fun PageDto.toEntity(): PageEntity = PageEntity(
    id = this.id ?: -1L,
    storyId = this.storyId ?: -1L,
    pageText = this.pageText ?: "",
    imageUrl = this.imageUrl,
    isEndingPage = this.isEndingPage ?: false
)

// ChoiceDto -> ChoiceEntity
fun ChoiceDto.toEntity(): ChoiceEntity = ChoiceEntity(
    id = this.id ?: -1L,
    pageId = this.pageId ?: -1L,
    choiceText = this.choiceText ?: "",
    targetPageId = this.targetPageId
)

// List<StoryDto> -> List<StoryEntity>
fun List<StoryDto>.toStoryEntities(): List<StoryEntity> = this.map { it.toEntity() }

// List<PageDto> -> List<PageEntity>
fun List<PageDto>.toPageEntities(): List<PageEntity> = this.map { it.toEntity() }

// List<ChoiceDto> -> List<ChoiceEntity>
fun List<ChoiceDto>.toChoiceEntities(): List<ChoiceEntity> = this.map { it.toEntity() }

fun StoryDto.toEntities(): Triple<StoryEntity, List<PageEntity>, List<ChoiceEntity>> {
    return Triple(
        first = this.toEntity(),
        second = this.pages?.toPageEntities() ?: emptyList(),
        third = this.pages?.flatMap { page ->
            page.choices?.toChoiceEntities() ?: emptyList()
        } ?: emptyList()
    )
}