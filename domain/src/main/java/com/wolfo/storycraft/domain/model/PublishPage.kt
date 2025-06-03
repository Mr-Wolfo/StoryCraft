package com.wolfo.storycraft.domain.model

import java.io.File

data class PublishPage(
    val text: String,
    val imageFile: File?,
    val isEndingPage: Boolean,
    val choices: List<PublishChoice> = emptyList()
    // Порядок страниц не нужен явно здесь, он определяется порядком в списке
)