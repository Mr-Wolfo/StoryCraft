package com.wolfo.storycraft.presentation.ui.features.story_editor.models

import java.util.UUID

typealias DraftItemId = String
fun generateDraftItemId(): DraftItemId = UUID.randomUUID().toString()

// UI модель для редактирования черновика
data class EditableStoryDraft(
    val id: String = generateDraftItemId(), // ID черновика (используется для сохранения/загрузки/удаления)
    var title: String = "",
    var description: String? = null,
    var tags: List<String> = emptyList(),
    var coverImageLocalPath: String? = null, // Локальный Uri или путь к изображению (для UI)
    val pages: MutableList<EditablePageDraft> = mutableListOf(EditablePageDraft()) // Минимум одна страница при создании
) {
    // Вспомогательная функция для UI валидации перед публикацией
    fun validateForPublish(): List<String> {
        val errors = mutableListOf<String>()

        if (title.isBlank()) {
            errors.add("Название истории не может быть пустым.")
        }
        if (pages.isEmpty()) {
            errors.add("История должна содержать хотя бы одну страницу.")
        }

        val pageCount = pages.size
        var hasEndingPage = false

        pages.forEachIndexed { pageIndex, page ->
            if (page.text.isBlank()) {
                errors.add("Страница ${pageIndex + 1} не может быть пустой.")
            }

            if (page.isEndingPage) {
                hasEndingPage = true
                if (page.choices.isNotEmpty()) {
                    errors.add("Страница ${pageIndex + 1} помечена как концовка, но содержит выборы.")
                }
            } else {
                if (page.choices.isEmpty()) {
                    errors.add("Страница ${pageIndex + 1} не является концовкой и должна иметь хотя бы один выбор.")
                }
            }

            page.choices.forEachIndexed { choiceIndex, choice ->
                if (choice.text.isBlank()) {
                    errors.add("Выбор ${choiceIndex + 1} на странице ${pageIndex + 1} не может быть пустым.")
                }
                // targetPageIndex может быть null только если страница-концовка
                // или если выбор ведет никуда (что является ошибкой, если страница не концовка)
                if (!page.isEndingPage && choice.targetPageIndex == null) {
                    errors.add("Выбор ${choiceIndex + 1} на странице ${pageIndex + 1} должен указывать на следующую страницу.")
                } else if (choice.targetPageIndex != null && (choice.targetPageIndex!! < 0 || choice.targetPageIndex!! >= pageCount)) {
                    errors.add("Выбор ${choiceIndex + 1} на странице ${pageIndex + 1} указывает на неверный индекс целевой страницы: ${choice.targetPageIndex}. Допустимые индексы: от 0 до ${pageCount - 1}.")
                }
            }
        }

        if (!hasEndingPage) {
            errors.add("История должна содержать хотя бы одну страницу-концовку.")
        }

        // TODO: Add more complex UI validation if needed (e.g., cycles, unreachable pages - though complex)

        return errors
    }
}