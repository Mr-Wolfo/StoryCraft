package com.wolfo.storycraft.presentation.ui.features.story_editor.models

data class EditablePageDraft(
    val id: DraftItemId = generateDraftItemId(), // Клиентский ID (UUID String)
    var text: String = "",
    var imageLocalPath: String? = null,  // Локальный Uri или путь к изображению страницы (для UI)
    var isEndingPage: Boolean = false,
    val choices: MutableList<EditableChoiceDraft> = mutableListOf() // MutableList
)