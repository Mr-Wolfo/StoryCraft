package com.wolfo.storycraft.data.mapper

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wolfo.storycraft.data.local.db.dao.StoryBaseInfoDraft
import com.wolfo.storycraft.data.local.db.entity.ChoiceDraftEntity
import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity
import com.wolfo.storycraft.data.local.db.entity.PageDraftEntity
import com.wolfo.storycraft.data.local.db.entity.PageDraftWithChoices
import com.wolfo.storycraft.data.local.db.entity.PageEntity
import com.wolfo.storycraft.data.local.db.entity.PageWithChoices
import com.wolfo.storycraft.data.local.db.entity.ReviewEntity
import com.wolfo.storycraft.data.local.db.entity.ReviewWithAuthor
import com.wolfo.storycraft.data.local.db.entity.StoryDraftEntity
import com.wolfo.storycraft.data.local.db.entity.StoryDraftWithPagesAndChoices
import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import com.wolfo.storycraft.data.local.db.entity.StoryWithAuthorAndTags
import com.wolfo.storycraft.data.local.db.entity.TagEntity
import com.wolfo.storycraft.data.local.db.entity.UserEntity
import com.wolfo.storycraft.data.remote.dto.ChoiceCreateDto
import com.wolfo.storycraft.data.remote.dto.ChoiceDto
import com.wolfo.storycraft.data.remote.dto.PageCreateDto
import com.wolfo.storycraft.data.remote.dto.PageDto
import com.wolfo.storycraft.data.remote.dto.ReviewDto
import com.wolfo.storycraft.data.remote.dto.StoryBaseInfoDto
import com.wolfo.storycraft.data.remote.dto.StoryCreateJsonDataDto
import com.wolfo.storycraft.data.remote.dto.StoryFullDto
import com.wolfo.storycraft.data.remote.dto.TagDto
import com.wolfo.storycraft.data.remote.dto.UserDto
import com.wolfo.storycraft.data.remote.dto.UserRegisterRequestDto
import com.wolfo.storycraft.data.remote.dto.UserSimpleDto
import com.wolfo.storycraft.data.remote.dto.UserUpdateDto
import com.wolfo.storycraft.domain.model.PublishChoice
import com.wolfo.storycraft.domain.model.PublishContent
import com.wolfo.storycraft.domain.model.PublishPage
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.auth.UserRegisterRequest
import com.wolfo.storycraft.domain.model.draft.DraftChoice
import com.wolfo.storycraft.domain.model.draft.DraftContent
import com.wolfo.storycraft.domain.model.draft.DraftPage
import com.wolfo.storycraft.domain.model.story.Choice
import com.wolfo.storycraft.domain.model.story.Page
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.model.story.StoryFull
import com.wolfo.storycraft.domain.model.story.Tag
import com.wolfo.storycraft.domain.model.user.User
import com.wolfo.storycraft.domain.model.user.UserSimple
import com.wolfo.storycraft.domain.model.user.UserUpdate

// ==========================================
// --- DTO (Network) -> Entity (Database) ---
// ==========================================

/**
 * Преобразует [UserSimpleDto] (краткая инфо об авторе из списков) в [UserEntity].
 * Заполняет только доступные поля. Остальные будут null или значениями по умолчанию.
 */
fun UserSimpleDto.toAuthorEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        avatarUrl = this.avatarUrl,
        email = null,
        signature = null,
        createdAt = null,
        overallRating = null
    )
}

/**
 * Преобразует [UserDto] (полная информация о пользователе) в [UserEntity].
 */
fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        email = this.email,
        signature = this.signature,
        avatarUrl = this.avatarUrl,
        createdAt = this.createdAt,
        overallRating = this.overallRating
    )
}

/**
 * Преобразует [TagDto] в [TagEntity].
 */
fun TagDto.toEntity(): TagEntity {
    return TagEntity(
        id = this.id,
        name = this.name
    )
}

/**
 * Преобразует [StoryBaseInfoDto] (из списков) в [StoryEntity].
 * Сохраняет только ID автора.
 */
fun StoryBaseInfoDto.toEntity(): StoryEntity {
    return StoryEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        averageRating = this.averageRating,
        publishedTime = this.publishedTime,
        viewCount = this.viewCount,
        authorId = this.author.id
    )
}

/**
 * Преобразует [StoryFullDto] (полная информация) в [StoryEntity].
 * Сохраняет только ID автора. Теги и страницы мапятся отдельно.
 */
fun StoryFullDto.toStoryEntity(): StoryEntity {
    return StoryEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        averageRating = this.averageRating,
        publishedTime = this.publishedTime,
        viewCount = this.viewCount,
        authorId = this.author.id
    )
}

/**
 * Преобразует [PageDto] в [PageEntity].
 * Выборы мапятся отдельно.
 */
fun PageDto.toEntity(): PageEntity {
    return PageEntity(
        id = this.id,
        storyId = this.storyId,
        pageText = this.pageText,
        imageUrl = this.imageUrl,
        isEndingPage = this.isEndingPage
    )
}

/**
 * Преобразует [ChoiceDto] в [ChoiceEntity].
 */
fun ChoiceDto.toEntity(pageId: String): ChoiceEntity {
    return ChoiceEntity(
        id = this.id,
        pageId = pageId,
        choiceText = this.choiceText,
        targetPageId = this.targetPageId
    )
}

/**
 * Преобразует [ReviewDto] в [ReviewEntity].
 * Сохраняет ID пользователя (автора отзыва).
 */
fun ReviewDto.toEntity(): ReviewEntity {
    return ReviewEntity(
        id = this.id,
        rating = this.rating,
        reviewText = this.reviewText,
        storyId = this.storyId,
        userId = this.userId,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}


// ==========================================
// --- Entity / Relation -> Domain Model ---
// ==========================================

/**
 * Преобразует [UserEntity] (из БД) в [User] (доменная модель).
 * Предоставляет значения по умолчанию для nullable полей, если они null в БД.
 */
fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email ?: "",
        signature = this.signature,
        avatarUrl = this.avatarUrl,
        createdAt = this.createdAt ?: "",
        stories = emptyList(), // Загружается отдельно
        overallRating = this.overallRating ?: 0f
    )
}

/**
 * Преобразует [UserEntity] в [UserSimple] (доменная модель для краткой информации).
 */
fun UserEntity.toUserSimple(): UserSimple {
    return UserSimple(
        id = this.id,
        username = this.username,
        avatarUrl = this.avatarUrl
    )
}

/**
 * Преобразует [TagEntity] в [Tag] (доменная модель).
 */
fun TagEntity.toDomain(): Tag {
    return Tag(
        id = this.id,
        name = this.name
    )
}

/**
 * Преобразует [StoryWithAuthorAndTags] (результат Room запроса) в [StoryBaseInfo] (доменная модель).
 * Обрабатывает возможный null для автора.
 */
fun StoryWithAuthorAndTags.toDomainBaseInfo(): StoryBaseInfo {
    // Создаем UserSimple по умолчанию на случай, если автор null
    val defaultAuthor = UserSimple(id = this.story.authorId ?: "unknown", username = "Unknown Author", avatarUrl = null)
    return StoryBaseInfo(
        id = this.story.id,
        title = this.story.title,
        description = this.story.description,
        coverImageUrl = this.story.coverImageUrl,
        averageRating = this.story.averageRating,
        publishedTime = this.story.publishedTime,
        viewCount = this.story.viewCount,
        author = this.author?.toUserSimple() ?: defaultAuthor, // Используем дефолтного, если null
        tags = this.tags.map { it.toDomain() }
    )
}

/**
 * Преобразует [ChoiceEntity] в [Choice] (доменная модель).
 */
fun ChoiceEntity.toDomain(): Choice {
    return Choice(
        id = this.id,
        pageId = this.pageId,
        choiceText = this.choiceText,
        targetPageId = this.targetPageId
    )
}

/**
 * Преобразует [PageWithChoices] (результат Room запроса) в [Page] (доменная модель).
 * Маппит вложенные выборы.
 */
fun PageWithChoices.toDomain(): Page {
    return Page(
        id = this.page.id,
        pageText = this.page.pageText,
        imageUrl = this.page.imageUrl,
        isEndingPage = this.page.isEndingPage,
        storyId = this.page.storyId,
        choices = this.choices.map { it.toDomain() }
    )
}

/**
 * Преобразует [ReviewWithAuthor] (результат Room запроса) в [Review] (доменная модель).
 * Обрабатывает возможный null для автора.
 */
fun ReviewWithAuthor.toDomain(): Review {
    // Создаем UserSimple по умолчанию
    val defaultAuthor = UserSimple(id = this.review.userId, username = "Unknown User", avatarUrl = null)
    return Review(
        id = this.review.id,
        rating = this.review.rating,
        reviewText = this.review.reviewText,
        storyId = this.review.storyId,
        userId = this.review.userId,
        user = this.author?.toUserSimple() ?: defaultAuthor, // Используем дефолтного, если null
        createdAt = this.review.createdAt,
        updatedAt = this.review.updatedAt
    )
}

/**
 * Собирает полную доменную модель [StoryFull] из отдельных частей, полученных из БД.
 * Принимает [StoryWithAuthorAndTags] и список [PageWithChoices].
 *
 * @param storyRelation Основная информация об истории с автором и тегами.
 * @param pagesRelation Список страниц с их выборами для этой истории.
 * @return Собранная [StoryFull] доменная модель.
 */
fun mapToStoryFullDomain(storyRelation: StoryWithAuthorAndTags, pagesRelation: List<PageWithChoices>): StoryFull {
    val defaultAuthor = UserSimple(id = storyRelation.story.authorId ?: "unknown", username = "Unknown Author", avatarUrl = null)
    return StoryFull(
        id = storyRelation.story.id,
        title = storyRelation.story.title,
        description = storyRelation.story.description,
        coverImageUrl = storyRelation.story.coverImageUrl,
        averageRating = storyRelation.story.averageRating,
        publishedTime = storyRelation.story.publishedTime,
        viewCount = storyRelation.story.viewCount,
        author = storyRelation.author?.toUserSimple() ?: defaultAuthor,
        tags = storyRelation.tags.map { it.toDomain() },
        pages = pagesRelation.map { it.toDomain() }
    )
}


// ==========================================
// --- Domain Model -> DTO (Network) ---
// ==========================================

/**
 * Преобразует [UserRegisterRequest] (доменная модель) в [UserRegisterRequestDto] (для API).
 */
fun UserRegisterRequest.toDto(): UserRegisterRequestDto {
    return UserRegisterRequestDto(
        username = this.username,
        email = this.email,
        password = this.password
    )
}

/**
 * Преобразует [UserUpdate] (доменная модель) в [UserUpdateDto] (для API).
 */
fun UserUpdate.toDto(): UserUpdateDto {
    return UserUpdateDto(
        email = this.email,
        signature = this.signature
    )
}

// ==========================================
// --- DTO -> Domain Model (Прямые мапперы, если не используется кэш) ---
// ==========================================
// ДЛЯ ОПЕРАЦИЙ, КОТОРЫЕ НЕ КЭШИРУЮТСЯ В БД

/**
 * Преобразует [UserSimpleDto] напрямую в [UserSimple] (доменная модель).
 */
fun UserSimpleDto.toDomain(): UserSimple {
    return UserSimple(
        id = this.id,
        username = this.username,
        avatarUrl = this.avatarUrl
    )
}

/**
 * Преобразует [TagDto] напрямую в [Tag] (доменная модель).
 */
fun TagDto.toDomain(): Tag {
    return Tag(
        id = this.id,
        name = this.name
    )
}

/**
 * Преобразует [StoryBaseInfoDto] напрямую в [StoryBaseInfo] (доменная модель).
 */
fun StoryBaseInfoDto.toDomain(): StoryBaseInfo {
    return StoryBaseInfo(
        id = this.id,
        title = this.title,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        averageRating = this.averageRating,
        publishedTime = this.publishedTime,
        viewCount = this.viewCount,
        author = this.author.toDomain(),
        tags = this.tags.map { it.toDomain() }
    )
}

/**
 * Преобразует [ChoiceDto] напрямую в [Choice] (доменная модель).
 * Требует передачи [pageId].
 */
fun ChoiceDto.toDomain(pageId: String): Choice {
    return Choice(
        id = this.id,
        pageId = pageId,
        choiceText = this.choiceText,
        targetPageId = this.targetPageId
    )
}

/**
 * Преобразует [PageDto] напрямую в [Page] (доменная модель).
 */
fun PageDto.toDomain(): Page {
    return Page(
        id = this.id,
        pageText = this.pageText,
        imageUrl = this.imageUrl,
        isEndingPage = this.isEndingPage,
        storyId = this.storyId,
        choices = this.choices.map { it.toDomain(this.id) }
    )
}

/**
 * Преобразует [ReviewDto] напрямую в [Review] (доменная модель).
 */
fun ReviewDto.toDomain(): Review {
    return Review(
        id = this.id,
        rating = this.rating,
        reviewText = this.reviewText,
        storyId = this.storyId,
        userId = this.userId,
        user = this.user.toDomain(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Преобразует [StoryFullDto] напрямую в [StoryFull] (доменная модель).
 */
fun StoryFullDto.toDomain(): StoryFull {
    return StoryFull(
        id = this.id,
        title = this.title,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        averageRating = this.averageRating,
        publishedTime = this.publishedTime,
        viewCount = this.viewCount,
        author = this.author.toDomain(),
        tags = this.tags.map { it.toDomain() },
        pages = this.pages.map { it.toDomain() }
    )
}

/**
 * Преобразует [UserDto] напрямую в [User] (доменная модель).
 */
fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        signature = this.signature,
        avatarUrl = this.avatarUrl,
        createdAt = this.createdAt,
        stories = this.stories.map { it.toDomainBaseInfoForUser() },
        overallRating = this.overallRating
    )
}

/**
 * Преобразует DTO `StoryBaseInfoForUser` (которое может содержаться внутри `UserDto`)
 * в доменную модель `StoryBaseInfo`.
 */
fun StoryBaseInfoDto.toDomainBaseInfoForUser(): StoryBaseInfo {
    return StoryBaseInfo(
        id = this.id,
        title = this.title,
        description = this.description,
        coverImageUrl = this.coverImageUrl,
        averageRating = this.averageRating,
        publishedTime = this.publishedTime,
        viewCount = this.viewCount,
        author = this.author.toDomain(),
        tags = this.tags.map { it.toDomain() }
    )
}

// ==========================================
// --- Domain Model -> Entity (Database) ---
// ==========================================

/**
 * Преобразует [User] (доменная модель) в [UserEntity].
 * Cсписок историй не сохраняется в UserEntity.
 */
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        email = this.email,
        signature = this.signature,
        avatarUrl = this.avatarUrl,
        createdAt = this.createdAt,
        overallRating = this.overallRating
    )
}

/**
 * Преобразует [UserSimple] (доменная модель) в [UserEntity].
 * Используется, когда есть только базовая информация (например, автор).
 */
fun UserSimple.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        avatarUrl = this.avatarUrl,
        email = null,
        signature = null,
        createdAt = null,
        overallRating = null
    )
}

/**
 * Преобразует [Tag] (доменная модель) в [TagEntity].
 */
fun Tag.toEntity(): TagEntity {
    return TagEntity(
        id = this.id,
        name = this.name
    )
}


// ==========================================================
// --- Mappers: Domain Model (DraftContent) -> Draft Entity ---
// ==========================================================
// Эти мапперы преобразуют Domain модель DraftContent для сохранения в локальной БД черновиков (Entity).

// Класс маппера для сохранения черновиков, требующий Gson для работы с тегами
class DraftContentToStoryDraftEntityMapper(private val gson: Gson) {

    /**
     * Преобразует [DraftContent] (Domain) в [StoryDraftEntity] (Entity).
     */
    fun map(draftContent: DraftContent): StoryDraftEntity {
        val tagsJson = gson.toJson(draftContent.tags)
        return StoryDraftEntity(
            id = draftContent.id,
            userId = draftContent.userId,
            title = draftContent.title,
            description = draftContent.description,
            tagsJson = tagsJson,
            coverImageUri = draftContent.coverImagePath?.let { Uri.parse(it) },
            lastSavedTimestamp = System.currentTimeMillis() // Обновляем метку времени при маппинге для сохранения
        )
    }

    /**
     * Преобразует список страниц из [DraftContent] (Domain) в список [PageDraftEntity] (Entity).
     * Порядок страниц берется из Domain модели.
     */
    fun mapPages(draftContent: DraftContent): List<PageDraftEntity> {
        return draftContent.pages.map { page ->
            PageDraftEntity(
                id = page.id,
                storyDraftId = draftContent.id,
                text = page.text,
                imageUri = page.imagePath?.let { Uri.parse(it) },
                isEndingPage = page.isEndingPage,
                pageOrder = page.pageOrder
            )
        }
    }

    /**
     * Преобразует список выборов из [DraftContent] (Domain) в список [ChoiceDraftEntity] (Entity).
     * Проходит по всем страницам Domain модели и собирает выборы.
     */
    fun mapChoices(draftContent: DraftContent): List<ChoiceDraftEntity> {
        return draftContent.pages.flatMap { page ->
            page.choices.map { choice ->
                ChoiceDraftEntity(
                    id = choice.id,
                    pageDraftId = page.id,
                    text = choice.text,
                    targetPageIndex = choice.targetPageIndex
                )
            }
        }
    }
}


// ============================================================
// --- Mappers: Domain Model (PublishContent) -> Remote DTO (Create) ---
// ============================================================
// Этот маппер преобразует Domain модель PublishContent в сетевые DTO для создания истории.

class PublishContentToStoryCreateDtoMapper {

    /**
     * Преобразует [PublishContent] (Domain) в [StoryCreateJsonDataDto] (DTO для JSON части API).
     * Файлы изображений (File) передаются отдельно в RemoteDataSource.
     */
    fun mapJsonData(publishContent: PublishContent): StoryCreateJsonDataDto {
        return StoryCreateJsonDataDto(
            title = publishContent.title,
            description = publishContent.description,
            tags = publishContent.tags,
            pages = publishContent.pages.map { it.toPageCreateDto() }
        )
    }

    private fun PublishPage.toPageCreateDto(): PageCreateDto {
        return PageCreateDto(
            pageText = text,
            isEndingPage = isEndingPage,
            choices = choices.map { it.toChoiceCreateDto() }
        )
    }

    private fun PublishChoice.toChoiceCreateDto(): ChoiceCreateDto {
        return ChoiceCreateDto(
            choiceText = text,
            targetPageIndex = targetPageIndex
        )
    }
}


// ==========================================================
// --- Mappers: Draft Entity -> Domain Model (DraftContent) ---
// ==========================================================
// Эти мапперы преобразуют данные черновика из локальной БД (Entity) в Domain модель DraftContent.

// Класс маппера для черновиков, требующий Gson для работы с тегами
class StoryDraftEntityToDraftContentMapper(private val gson: Gson) {

    /**
     * Преобразует [StoryDraftWithPagesAndChoices] (Entity с отношениями) в [DraftContent] (Domain).
     */
    fun map(draftWithDetails: StoryDraftWithPagesAndChoices): DraftContent {
        val tagsType = object : TypeToken<List<String>>() {}.type
        val tags: List<String> = try {
            gson.fromJson(draftWithDetails.storyDraft.tagsJson, tagsType) ?: emptyList()
        } catch (e: Exception) {
            // В случае ошибки парсинга возвращаем пустой список
            emptyList()
        }

        return DraftContent(
            id = draftWithDetails.storyDraft.id,
            userId = draftWithDetails.storyDraft.userId,
            title = draftWithDetails.storyDraft.title,
            description = draftWithDetails.storyDraft.description,
            tags = tags,
            coverImagePath = draftWithDetails.storyDraft.coverImageUri?.toString(),
            pages = draftWithDetails.pagesWithChoices
                .sortedBy { it.pageDraft.pageOrder }
                .map { it.toDraftPage() },
            lastSavedTimestamp = draftWithDetails.storyDraft.lastSavedTimestamp
        )
    }

    private fun PageDraftWithChoices.toDraftPage(): DraftPage {
        return DraftPage(
            id = pageDraft.id,
            text = pageDraft.text,
            imagePath = pageDraft.imageUri?.toString(),
            isEndingPage = pageDraft.isEndingPage,
            choices = choices.map { it.toDraftChoice() },
            pageOrder = pageDraft.pageOrder
        )
    }

    private fun ChoiceDraftEntity.toDraftChoice(): DraftChoice {
        return DraftChoice(
            id = id,
            text = text,
            targetPageIndex = targetPageIndex
        )
    }

    /**
     * Преобразует [StoryBaseInfoDraft] (Projection из Room) в [StoryBaseInfo] (Domain).
     * Используется для списка черновиков.
     */
    fun mapBaseInfo(draft: StoryBaseInfoDraft): StoryBaseInfo {
        // Draft Projection не содержит всех полей StoryBaseInfo.
        // Используем значения по умолчанию или null для недостающих полей.
        return StoryBaseInfo(
            id = draft.id,
            title = draft.title,
            description = null, // Нет в Projection
            coverImageUrl = null, // Нет в Projection
            averageRating = 0f, // Значение по умолчанию
            publishedTime = draft.lastSavedTimestamp.toString(), // Используем timestamp сохранения как временный "publishedTime"
            viewCount = 0,
            author = UserSimple(id = "", username = "Черновик", avatarUrl = null), // Заглушка автора для списка
            tags = emptyList() // Нет в Projection
        )
    }
}



