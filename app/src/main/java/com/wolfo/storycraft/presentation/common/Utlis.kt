package com.wolfo.storycraft.presentation.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object Utils {
    fun toLocaleDateTime(time: String): LocalDateTime =
        try {
            Instant.parse(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        } catch (e: Exception) {
            LocalDateTime.now()
        }
}