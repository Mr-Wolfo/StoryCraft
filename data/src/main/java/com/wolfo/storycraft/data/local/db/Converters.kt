package com.wolfo.storycraft.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wolfo.storycraft.data.local.db.entity.AuthorEntity
import java.util.Date

object DateConverter {
    @TypeConverter
    @JvmStatic // Важно для Room
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    @JvmStatic
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}

object ListConverter {
    private val gson = Gson() // Используем Gson для сериализации списка строк

    @TypeConverter
    @JvmStatic
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    @JvmStatic
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, listType)
        }
    }
}

object AuthorConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAuthorEntity(author: AuthorEntity?): String? {
        return author?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toAuthorEntity(json: String?): AuthorEntity? {
        return json?.let { gson.fromJson(it, object : TypeToken<AuthorEntity>() {}.type) }
    }
}