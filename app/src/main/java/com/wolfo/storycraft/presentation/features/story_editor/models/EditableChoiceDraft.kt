package com.wolfo.storycraft.presentation.features.story_editor.models

data class EditableChoiceDraft(
    val id: DraftItemId = generateDraftItemId(), // Клиентский ID (UUID String)
    var text: String = "",
    var targetPageIndex: Int? = null // Целевая страница по индексу
)