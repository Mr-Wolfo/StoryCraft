package com.wolfo.storycraft.data.mapper

import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity
import com.wolfo.storycraft.data.local.db.entity.PageEntity
import com.wolfo.storycraft.data.local.db.entity.PageWithChoices
import com.wolfo.storycraft.data.local.db.entity.ReviewEntity
import com.wolfo.storycraft.data.local.db.entity.ReviewWithAuthor
import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import com.wolfo.storycraft.data.local.db.entity.StoryWithAuthorAndTags
import com.wolfo.storycraft.data.local.db.entity.TagEntity
import com.wolfo.storycraft.data.local.db.entity.UserEntity
import com.wolfo.storycraft.data.remote.dto.ChoiceDto
import com.wolfo.storycraft.data.remote.dto.PageDto
import com.wolfo.storycraft.data.remote.dto.ReviewDto
import com.wolfo.storycraft.data.remote.dto.StoryBaseInfoDto
import com.wolfo.storycraft.data.remote.dto.StoryFullDto
import com.wolfo.storycraft.data.remote.dto.TagDto
import com.wolfo.storycraft.data.remote.dto.UserDto
import com.wolfo.storycraft.data.remote.dto.UserRegisterRequestDto
import com.wolfo.storycraft.data.remote.dto.UserSimpleDto
import com.wolfo.storycraft.data.remote.dto.UserUpdateDto
import com.wolfo.storycraft.domain.model.Choice
import com.wolfo.storycraft.domain.model.Page
import com.wolfo.storycraft.domain.model.Review
import com.wolfo.storycraft.domain.model.StoryBaseInfo
import com.wolfo.storycraft.domain.model.StoryFull
import com.wolfo.storycraft.domain.model.Tag
import com.wolfo.storycraft.domain.model.User
import com.wolfo.storycraft.domain.model.UserRegisterRequest
import com.wolfo.storycraft.domain.model.UserSimple
import com.wolfo.storycraft.domain.model.UserUpdate

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
        createdAt = this.createdAt, // Предполагаем, что это строка ISO 8601
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
        publishedTime = this.publishedTime, // Сохраняем строку ISO 8601
        viewCount = this.viewCount,
        authorId = this.author.id // Сохраняем только ID автора
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
        authorId = this.author.id // Сохраняем только ID автора
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
 * Требует передачи [pageId], так как в [ChoiceDto] его нет по OpenAPI схеме.
 */
fun ChoiceDto.toEntity(pageId: String): ChoiceEntity {
    return ChoiceEntity(
        id = this.id,
        pageId = pageId, // Используем переданный ID страницы
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
        userId = this.userId, // или this.user.id, зависит от DTO (здесь userId есть явно)
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
 * Список историй обычно загружается отдельно, поэтому здесь он пуст.
 */
fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email ?: "", // Значение по умолчанию
        signature = this.signature, // Nullable в доменной модели тоже
        avatarUrl = this.avatarUrl,
        createdAt = this.createdAt ?: "", // Значение по умолчанию
        stories = emptyList(), // Загружается отдельно
        overallRating = this.overallRating ?: 0f // Значение по умолчанию
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
    // Создаем UserSimple по умолчанию на случай, если автор null (из-за SET_NULL или данных)
    val defaultAuthor = UserSimple(id = this.story.authorId ?: "unknown", username = "Unknown Author", avatarUrl = null)
    return StoryBaseInfo(
        id = this.story.id,
        title = this.story.title,
        description = this.story.description,
        coverImageUrl = this.story.coverImageUrl,
        averageRating = this.story.averageRating,
        publishedTime = this.story.publishedTime, // Оставляем строкой или форматируем в Date/String
        viewCount = this.story.viewCount,
        author = this.author?.toUserSimple() ?: defaultAuthor, // Используем дефолтного, если null
        tags = this.tags.map { it.toDomain() } // Маппим теги
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
        choices = this.choices.map { it.toDomain() } // Маппим выборы
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
        pages = pagesRelation.map { it.toDomain() } // Маппим страницы с выборами
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
        email = this.email, // Nullable поля остаются nullable
        signature = this.signature
    )
}

// ==========================================
// --- DTO -> Domain Model (Прямые мапперы, если не используется кэш) ---
// ==========================================
// Эти мапперы могут быть полезны, если в каком-то сценарии данные из сети
// не проходят через базу данных (Entity), а сразу идут в Domain слой.
// Например, для операций, которые не кэшируются.

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
        author = this.author.toDomain(), // Маппим вложенный UserSimpleDto
        tags = this.tags.map { it.toDomain() } // Маппим вложенные TagDto
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
        choices = this.choices.map { it.toDomain(this.id) } // Маппим вложенные ChoiceDto, передавая ID страницы
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
        user = this.user.toDomain(), // Маппим вложенный UserSimpleDto
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
        author = this.author.toDomain(), // Маппим UserSimpleDto
        tags = this.tags.map { it.toDomain() }, // Маппим TagDto
        pages = this.pages.map { it.toDomain() } // Маппим PageDto
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
        stories = this.stories.map { it.toDomainBaseInfoForUser() }, // Используем специфичный маппер для StoryBaseInfoForUser
        overallRating = this.overallRating
    )
}

/**
 * Преобразует DTO `StoryBaseInfoForUser` (которое может содержаться внутри `UserDto`)
 * в доменную модель `StoryBaseInfo`.
 * Обратите внимание: эта DTO может иметь меньше полей, чем `StoryBaseInfoDto`.
 * Если поля совпадают, можно переиспользовать `StoryBaseInfoDto.toDomain()`.
 * Если нет, создаем отдельный маппер.
 */
fun StoryBaseInfoDto.toDomainBaseInfoForUser(): StoryBaseInfo {
    // Предполагаем, что StoryBaseInfoForUserDto имеет поля, достаточные для StoryBaseInfo Domain,
    // но в Domain модели User в поле stories хранится именно StoryBaseInfo.
    // Если у User в Domain модели должно быть поле List<StoryBaseInfoForUser>, то нужно создать такую модель
    // и маппер к ней. Пока маппим в StoryBaseInfo, заполняя недостающие поля null/default.
    return StoryBaseInfo(
        id = this.id,
        title = this.title,
        description = null, // Нет в StoryBaseInfoForUserDto
        coverImageUrl = this.coverImageUrl,
        averageRating = this.averageRating,
        publishedTime = this.publishedTime,
        viewCount = 0, // Нет в StoryBaseInfoForUserDto
        author = UserSimple(id = "unknown", username = "Unknown", avatarUrl = null), // Автора здесь нет, берем из контекста UserDto, если нужно
        tags = emptyList() // Тегов здесь нет
    )
}

// ==========================================
// --- Domain Model -> Entity (Database) ---
// ==========================================
// Может понадобиться, если вы создаете/обновляете Entity напрямую из Domain модели,
// например, при сохранении созданной пользователем истории до отправки в сеть.

/**
 * Преобразует [User] (доменная модель) в [UserEntity].
 * Обратите внимание на список историй - он не сохраняется в UserEntity.
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