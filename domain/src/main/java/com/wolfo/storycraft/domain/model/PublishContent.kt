package com.wolfo.storycraft.domain.model

import java.io.File

data class PublishContent(
    val id: String, // ID черновика, из которого публикуем (для удаления после успеха)
    val title: String,
    val description: String?,
    val tags: List<String>,
    val coverImageFile: File?,
    val pages: List<PublishPage> = emptyList()
) {
    fun validateDomain(): List<String> {
        val errors = mutableListOf<String>()
        // TODO: Implement more complex domain validation
        return errors
    }
}